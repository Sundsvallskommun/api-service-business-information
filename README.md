# Business Information

## Leverantör

Sundsvalls Kommun

## Beskrivning

Tillhandahåller information om livsmedelsanläggningar

## Tekniska detaljer

### Konfiguration

Konfiguration sker i filen `src/main/resources/application.properties` genom att sätta nedanstående
properties till önskade värden:

| Property                    | Beskrivning                  |
|-----------------------------|------------------------------|
| `integration.ecos.url`      | URL till ECOS-miljön         
| `integration.ecos.username` | Användarnamn för ECOS-miljön 
| `integration.ecos.password` | Lösenord för ECOS-miljön     

### Paketera och starta tjänsten

Paketera tjänsten som en körbar JAR-fil genom:

```
mvn package
```

Starta med:

```
java -jar target/api-service-business-information-<VERSION>.jar
```

### Bygga och starta tjänsten med Docker

Bygg en Docker-image av tjänsten:

```
mvn spring-boot:build-image
```

Starta en Docker-container:

```
docker run -i --rm -p 8080:8080 evil.sundsvall.se/ms-business-information:latest
```

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-business-information&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-business-information)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-business-information&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-business-information)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-business-information&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-business-information)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-business-information&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-business-information)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-business-information&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-business-information)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-business-information&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-business-information)

##   

Copyright (c) 2021 Sundsvalls kommun
