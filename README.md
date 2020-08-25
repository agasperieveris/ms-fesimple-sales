# ms-fesimple-sales

Gestion de las Ventas
## Getting Started

TODO: These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads) - Programming Language.
* [Maven](https://maven.apache.org/) - Dependency Management.
* [Spring Boot](https://spring.io/projects/spring-boot) - Framework to microservices.

### Installing

Para la compilación, empaquetamiento e instalación del artefacto se ejecutan los siguientes comandos.
para el gestor dependencias Maven.
```
mvn clean
mvn install
```

para el gestor dependencias Gradle.
```
gradle clean
gradle build
```

## Running

Para la ejecución del microservicio ejecutamos el siguiente comando.
para el gestor dependencias Maven.
```
mvn spring-boot:run
```
para el gestor dependencias Gradle.
```
gradle bootRun
```

Para invocar el endpoint de pruebas se puede utilizar el comando curl.

```
curl -H "unica-serviceid: 550e8400-e29b-41d4-a716-446655440000" -H "unica-application:genesis" -H "unica-pid:550e8400-e29b-41d4-a716-446655440000" -H "unica-user:genesis" http://localhost:8080/sales/v1/greeting
```

En el caso que se quiera acceder al Swagger se requiere correr el microservicio con el profile dev o swagger. Por ejemplo:

para el gestor dependencias Maven.
```
mvn spring-boot:run -Dspring.profiles.active=swagger 
```
o
```
mvn spring-boot:run -Dspring.profiles.active=dev 
```
para el gestor dependencias Gradle.
```
gradle bootRun -Dspring.profiles.active=swagger 
```
o
```
gradle bootRun -Dspring.profiles.active=dev 
```

Y para consultar el Swagger se puede utilizar el comando curl.

```
curl  http://localhost:8080/v2/api-docs
```

Para visualizar el Swagger UI, pueden verlo en el siguiente enlace.

```
http://localhost:8080/swagger-ui.html
```

## Running the tests

Para la ejecución de los test ejecutamos el siguiente comando.
para el gestor dependencias Maven.
```
mvn test
```
para el gestor dependencias Gradle.
```
gradle test
```

## Para valuación de codigo fuente localmente
* checkstyle 
* pmd
* spotbugs 
* jacoco
```
gradle check
```
### Break down into end to end tests

TODO: Listar los test.

### And coding style tests

Para el evaluar el código se utilizan las siguiente herramientas.

* [CheckStyle](https://checkstyle.sourceforge.io/) - Para los estilos de código.
* [Spotbugs](https://spotbugs.github.io/) - Para el análisis estático de código.
* [PMD](https://pmd.github.io/) - Para el análisis estático de código.

Para invocar el análisis de código estático.

Maven:
```
mvn compile test site
```
Gradle:
```
gradle check
```


## Deployment

TODO: Add additional notes about how to deploy this on a live system

## Context Path

Cada microservicio debe tener un context path unico, para esto debe configurar el contexto de su microservicio
en el application.yml server.servlet.context-path

## Built With

* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads) - Programming Language.
* [Maven](https://maven.apache.org/) - Dependency Management.
* [Spring Boot](https://spring.io/projects/spring-boot) - Framework to microservices.
* [Genesis] - Framework para la implementación de Arquitectura Digital en Telefónica del Perú.

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Authors

* **everis SAC** - *Initial work* - [everis](https://www.everis.com/peru)