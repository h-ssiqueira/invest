# Kind
```shell
kind create cluster --name <name>
kind load docker-image <image>:<label> --name <name>
```
# Helm
## Installing
```shell
helm dependency update .

helm install bank .
```
### Checking
```shell
helm template <deployment> --values <deployment>/values.yaml > test.yml
```
## Upgrading
```shell
helm upgrade bank .
```
## Uninstalling
```shell
helm uninstall bank
```
