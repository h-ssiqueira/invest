{{/*
Expand the name of the chart.
*/}}
{{- define "investment.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "investment.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "investment.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "investment.labels" -}}
helm.sh/chart: {{ include "investment.chart" . }}
{{ include "investment.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "investment.selectorLabels" -}}
app.kubernetes.io/name: {{ include "investment.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "investment.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "investment.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Check if resources.requests is set and fail if it is.
This is to ensure that requests are not manually set, as they are calculated automatically from limits.
*/}}
{{- define "checkResourcesRequestsNotSet" -}}
{{- if hasKey .Values.resources "requests" }}
{{- fail "You must not set 'resources.requests' manually. The values are automatically calculated from 'resources.limits'." }}
{{- end -}}
{{- end -}}

{{/*
Calculate resources.requests based on resources.limits.
This is used to ensure that requests are always set based on the limits.
*/}}
{{- define "calculatedResourcesRequests" -}}
requests:
  cpu: {{ .Values.resources.limits.cpu | quote }}
  memory: {{ .Values.resources.limits.memory | quote }}
{{- end -}}