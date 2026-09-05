# charts/invitation

모바일 청첩장 백엔드를 DGX Spark 단일 노드 클러스터(arm64, kubeadm)의 `admin` 네임스페이스에
올리는 Helm 차트. 클러스터 자체의 IaC 는 `~/k8s` 레포가 단일 소스 오브 트루스이고,
이 차트는 그 컨벤션(워크로드는 admin ns, 접근은 NodePort, values 에 비밀 없음)을 따른다.

명령 블록이 곧 런북이다. 전부 이 레포 루트에서 실행한다.

## 전제

한 번만 하면 되는 것들이고, 이미 되어 있다.

- **DB**: `admin/postgres` (postgres:18) 의 `kihan_eunsun` 데이터베이스, `wedding-invitation` 스키마,
  테이블 4개(admin, guest, message, invitation_view). 원본은 `src/main/resources/schema.sql`.
  앱은 `ddl-auto=validate` + `spring.sql.init.mode=never` 라 **스키마를 만들지도, 고치지도 않는다.**
  스키마가 어긋나면 파드가 기동 중에 죽는다(런타임에 조용히 틀어지는 것보다 낫다).
- **JWT 서명키 Secret**: `admin/invitation-secret` 의 `JWT_ADMIN_SECRET`. 없으면 아래로 만든다.
  32바이트 이상이어야 한다 (`JwtTokenProvider` 가 UTF-8 바이트를 그대로 `Keys.hmacShaKeyFor` 에 넘긴다).

```bash
kubectl create secret generic invitation-secret -n admin \
  --from-literal=JWT_ADMIN_SECRET="$(openssl rand -hex 32)" \
  --dry-run=client -o yaml | kubectl apply -f -
```

DB 비밀번호는 별도로 만들지 않는다. postgres 가 쓰는 `admin/postgres-secret` 을 차트가 그대로
참조한다(같은 ns). 복사본을 두지 않는 이유는 `values.yaml` 의 secrets 블록 주석에 있다.

## 이미지 빌드

이 박스에는 레지스트리가 없다. 호스트에서 빌드해 containerd 의 `k8s.io` 네임스페이스에 직접
적재하고 kubelet 이 그것을 찾아 쓴다(`imagePullPolicy: Never`). docker 는 없고 nerdctl + buildkit 이다.

```bash
TAG=0.0.1   # 재배포할 때마다 올릴 것. 같은 태그를 덮어쓰면 롤백할 대상이 사라진다
sudo nerdctl --namespace k8s.io build -t wedding-invitation:$TAG .
sudo nerdctl --namespace k8s.io images | grep wedding-invitation
```

`Dockerfile` 은 멀티스테이지다. 빌드 스테이지가 `eclipse-temurin:17-jdk` 안에서 gradle wrapper 로
`bootJar` 를 만든다 - 호스트 JDK 는 8뿐이라 로컬 빌드가 불가능하고, 이렇게 두면 호스트에
JDK 17 을 깔지 않아도 된다. 런타임 스테이지는 JRE 이미지에 non-root 로 jar 하나만 얹는다.

## 배포

```bash
helm upgrade --install invitation charts/invitation -n admin \
  --set image.tag=$TAG --wait --timeout 5m
```

`--set image.tag` 를 생략하면 `Chart.yaml` 의 `appVersion` 이 태그가 된다. 태그를 올렸다면
`Chart.yaml` 의 `appVersion` 도 같이 올려서 기본값이 진실을 가리키게 둘 것.

## 확인

```bash
kubectl -n admin rollout status deploy/invitation
kubectl -n admin logs -l app.kubernetes.io/instance=invitation -f

# NodePort. 이 클러스터에 Ingress 컨트롤러가 없다.
curl -s "http://192.168.219.108:30808/api/invitation?token=<게스트토큰>&accessType=LINK"
curl -s -X POST http://192.168.219.108:30808/api/admin/login \
  -H 'Content-Type: application/json' -d '{"username":"...","password":"..."}'
```

기동에 성공했는지는 로그의 `Started InvitationApplication` 과 그 앞의 Hibernate 라인으로 본다.
프로브가 전부 `tcpSocket` 인 이유는 actuator 의존성이 없어서다(`values.yaml` probes 블록).
**포트가 열렸다 = 스프링 컨텍스트가 완성됐다** 가 성립하는 구조라서 이것으로 충분하다:
DB 검증 실패는 컨텍스트 생성 중에 나므로 포트가 열리기 전이고, 프로브에 그대로 걸린다.

## 외부 공개 (Ingress + TLS)

기본값은 `ingress.enabled=false` 다. 켜는 순간 cert-manager 가 Let's Encrypt 에 챌린지를 걸기
때문에, 아래 순서를 지키지 않으면 실패만 쌓이고 prod 레이트리밋(시간당 5회 실패 검증)에 걸린다.

**왜 HTTP-01 뿐인가**: 도메인이 가비아 NS 에 있고 cert-manager 에는 가비아 DNS-01 solver 가 없다
(내장은 Cloudflare/Route53/Azure/Google/DigitalOcean/RFC2136/ACME-DNS). TLS-ALPN-01 은
cert-manager 가 지원하지 않는다. 그래서 **inbound 80 이 열려 있어야만** 발급된다.

### 1. 손으로 해야 하는 것 (클러스터 밖)

- 공유기: **80 과 443 을 192.168.219.108 로 포워딩**. 443 만으로는 인증서를 못 받는다.
- 가비아 DNS: A 레코드 `api` -> 공인 IP (`curl -s ifconfig.me` 로 확인). DDNS 는 쓰지 않는다:
  공인 IP 변경은 Hermes 가 5분 크론으로 감시해 알리고, 그때 이 레코드만 손으로 바꾼다.
- ClusterIssuer `letsencrypt-prod` 는 k8s 레포가 관리한다(`~/k8s/manifests/ingress/`, 2026-09-05 적용).
  staging 발급자는 두지 않는다. 그 역할이던 "80 이 밖에서 통하는가" 는 아래 curl 로 대신 확인한다.

회선이 LG U+(AS9316) 가정용이라 inbound 80 이 막혀 있을 수 있다. 포워딩 후 **외부 망**
(휴대폰 LTE 등)에서 확인할 것. 사내망에서 공인 IP 로 치면 헤어핀 NAT 때문에 결과가 거짓일 수 있다.

```bash
curl -sI --max-time 8 http://api.kihan-eunsun.site/   # 404 면 컨트롤러까지 도달한 것
```

### 2. 발급 (2026-09-05 완료)

`values.yaml` 의 `ingress.enabled` 가 이미 `true` 라 차트를 올리면 Ingress 와 함께 cert-manager 가
`Certificate` 를 만들고 발급을 진행한다. 1번이 아직 안 돼 있어도 켜 두는 것이 안전하다:
cert-manager 는 자체 사전 점검(DNS 해석 + 챌린지 URL 도달)이 통과한 뒤에야 Let's Encrypt 에
검증을 요청하므로, 준비 전에는 prod 의 '시간당 5회 실패 검증' 한도를 소모하지 않고 기다린다.

```bash
kubectl apply -k ~/k8s/manifests/ingress/
helm upgrade --install invitation charts/invitation -n admin

kubectl -n admin get certificate invitation-tls        # READY=True 면 발급 완료
kubectl -n admin describe challenge | grep Reason      # pending 이면 여기에 이유가 남는다
curl -s https://api.kihan-eunsun.site/                 # Spring 의 JSON 404 가 오면 끝
```

**사전 점검이 `no such host` 에서 안 풀릴 때**: A 레코드를 만들기 전에 이름을 조회한 적이 있으면
LG U+ 리졸버(특히 61.41.153.2)가 "없음" 응답을 SOA TTL(최대 24h) 동안 캐시한다. 클러스터 DNS 는
그 리졸버로 나가므로 같이 막힌다. 기다리거나, CoreDNS 의 `forward` 를 잠시 정상 리졸버
(`1.214.68.2 8.8.8.8`)로 바꿔 발급을 끝내고 되돌린다(2026-09-05 에 그렇게 했다).

인증서는 90일짜리이고 cert-manager 가 만료 30일 전에 자동 갱신한다. 갱신도 HTTP-01 이라
**공유기 80 포워딩이 계속 살아 있어야 한다.**

프론트(Vercel)는 환경변수 `INVITATION_API_BASE_URL=https://api.kihan-eunsun.site` 로 이 주소를
읽는다(서버 컴포넌트에서 호출). CORS 는 이미 `https://kihan-eunsun.site` 와 `https://*.vercel.app`
이 허용돼 있어 백엔드는 손댈 것이 없다(`SecurityConfig.corsConfigurationSource`).

## 롤백 / 제거

```bash
helm history invitation -n admin
helm rollback invitation <REVISION> -n admin
helm uninstall invitation -n admin   # Secret 은 차트 밖이라 남는다
```
