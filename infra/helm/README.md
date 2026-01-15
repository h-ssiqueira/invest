# Kind
```shell
kind create cluster --name <name>
kind load docker-image <image>:<label> --name <name>
```
# Helm
## Installing
* Check the credentials related to Kaggle and database before applying these commands.
```shell
helm dependency update .

helm install <name> .
```
### Checking
```shell
helm template <deployment> . --values <deployment>/values.yaml --debug
```
## Upgrading
```shell
helm upgrade <deployment> .
```
## Uninstalling
```shell
helm uninstall <deployment>
```
