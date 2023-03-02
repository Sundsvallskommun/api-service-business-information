# Business Information
## Leverantör
Sundsvalls Kommun

## Beskrivning
Tillhandahåller information om livsmedelsanläggningar

## Tekniska detaljer

### Konfiguration

Konfiguration sker i filen `src/main/resources/application.properties` genom att sätta nedanstående properties till önskade värden:

|Property|Beskrivning|
|---|---|
|`integration.ecos.url`| URL till ECOS-miljön
|`integration.ecos.username`| Användarnamn för ECOS-miljön
|`integration.ecos.password`| Lösenord för ECOS-miljön

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


Copyright &copy; 2022 Sundsvalls Kommun
