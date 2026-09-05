{{/*
리소스 이름 = 릴리스 이름. 이 차트는 릴리스 하나 = 앱 하나라 접미사를 붙이지 않는다
(서비스 DNS 가 곧 릴리스 이름이 되어 클러스터 안 주소가 예측 가능해진다).
*/}}
{{- define "invitation.name" -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "invitation.labels" -}}
app.kubernetes.io/name: invitation
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version }}
{{- end -}}

{{- define "invitation.selectorLabels" -}}
app.kubernetes.io/name: invitation
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- define "invitation.image" -}}
{{- printf "%s:%s" .Values.image.repository (.Values.image.tag | default .Chart.AppVersion) -}}
{{- end -}}
