# Invest

![Docker](https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Java 23](https://img.shields.io/badge/java%2021-000000?style=for-the-badge&logo=openjdk&logoColor=white)
![Postgresql](https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Kubernetes](https://img.shields.io/badge/kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)

![Kaggle](https://img.shields.io/badge/kaggle-20BEFF?style=for-the-badge&logo=kaggle&logoColor=white)
![Maven](https://img.shields.io/badge/maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

![Liquibase](https://img.shields.io/badge/liquibase-2962FF?style=for-the-badge&logo=liquibase&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

![Junit5](https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Swagger](https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

## Description
Investment simulation and storage for different types of fixed income:
Name | Description | Has taxes
:---: | :---: | :---:
CDB | *Certificado de Depósito Bancário* | **:heavy_check_mark:**
RDB | *Recibo de Depósito Bancário* | **:heavy_check_mark:**
LCA | *Letra de Crédito do Agronegócio* | **:x:**
LCI | *Letra de Crédito Imobiliário* | **:x:**
CRA | *Certificado de Recebíveis do Agronegócio* | **:x:**
CRI | *Certificado de Recebíveis Imobiliários* | **:x:**
### Investment Aliquot Type
Type | Format | Example
:---: | :---: | :---:
PREFIXED | `x%` | 10%
INFLATION | `IPCA + x%` | IPCA + 5%
POSTFIXED | `x%` | 110% (SELIC based)
* SELIC and IPCA are retrieved from [this dataset](https://www.kaggle.com/datasets/hssiqueira/brazil-interest-rate-history-selic).
* Interest rates are being updated on:
    * Every application startup;
    * Every API call `[POST] /api/v1/rates`;
    * Every day by CRON.
## Executing
* Kaggle API credentials is required to populate the database to perform SELIC and IPCA calculations.

Generate the docker image with:
```shell
mvn clean package -Pdocker
```
* Adjust database connection and other properties to execute the application.
### Kubernetes
Check [this README](/helm/README.md)
### Architecture

<img src="docs/architecture.svg" alt="architecture" />
