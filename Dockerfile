# syntax=docker/dockerfile:1
#
# 청첩장 백엔드 이미지. 빌드와 실행을 두 단계로 나눈다.
#   1) builder : JDK 17 + Gradle 로 bootJar 생성
#   2) runtime : JRE 17 만 있는 이미지에 jar 하나만 복사
# 결과 이미지에는 소스, Gradle, JDK 가 없다. 배포 절차는 charts/invitation/README.md.

# ---------------------------------------------------------------------------
# 1) builder
# ---------------------------------------------------------------------------
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace

# 레이어 순서는 "잘 안 바뀌는 것 -> 자주 바뀌는 것" 이다. 소스보다 먼저 복사한 레이어는
# 소스만 바뀐 빌드에서 캐시로 재사용된다.

# Gradle 배포판 다운로드. wrapper 파일만 바뀌지 않으면 캐시된다.
COPY gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --version --no-daemon

# 의존성 다운로드. build.gradle 이 바뀌지 않으면 캐시된다.
# `|| true` 는 이 단계가 캐시 워밍일 뿐이라서다: 여기서 실패해도 아래 bootJar 가 다시 받는다.
COPY settings.gradle build.gradle ./
RUN ./gradlew dependencies --no-daemon || true

# 소스 컴파일. 테스트는 DB 가 필요해 이미지 빌드에서는 돌리지 않는다.
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ---------------------------------------------------------------------------
# 2) runtime
# ---------------------------------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app

# root 로 실행하지 않는다. 컨테이너 탈출 시 피해 범위를 줄이는 기본 조치.
RUN groupadd -r app && useradd -r -g app app

# bootJar 산출물은 하나뿐이다(build.gradle 의 version = 0.0.1-SNAPSHOT).
# 버전 규칙이 바뀌면 이 패턴도 같이 바꿔야 한다.
COPY --from=builder /workspace/build/libs/*-SNAPSHOT.jar /app/app.jar
RUN chown -R app:app /app

USER app
EXPOSE 8080

# 이미지 기본값. 실제 배포에서는 차트(values.yaml 의 javaOpts)가 같은 이름의 env 로 덮어쓴다.
#   MaxRAMPercentage : 컨테이너 메모리 limit 기준으로 힙 상한을 잡는다(고정 -Xmx 대신).
#   user.timezone    : created_at 을 앱과 DB 가 같은 시간대로 만들게 한다.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Seoul"

# sh -c 를 거치는 이유는 $JAVA_OPTS 를 단어 단위로 풀기 위해서다. exec 로 교체해야
# java 가 PID 1 이 되어 컨테이너 종료 신호(SIGTERM)를 직접 받는다.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
