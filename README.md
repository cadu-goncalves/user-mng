## Description

User management microservice with following features:

- CRUD endpoints 
- Search endpoint with pagination, sorting and filters
- User authentication and authorization
- Role based access for **ADMIN** and **USER**
- Restricted data change operations to ADMIN users
- Password encryption
- Data persistence to [MonogoDB](https://www.mongodb.com/)
- Oline API Documentation

## Dependencies

#### Runtime

* [Java 8 or above](http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html)

#### Developement

* [Java JDK 8 or above](http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html)
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)


## Structure

#### Main Dependencies

* [Spring Boot](https://spring.io/projects/spring-boot) Framework integration, dependency injection, MVC
* [Spring Data](https://spring.io/projects/spring-data) Data access model
* [Spring Security](https://spring.io/projects/spring-security) Security, authentication/authorization
* [Undertow](http://undertow.io/) Web server
* [Mongobee](https://github.com/mongobee/mongobee)  Data migrations for MongoDB
* [Springfox](https://springfox.github.io/springfox/) Provides API automated documentation based on definitions pointed by [Swagger](https://swagger.io/)

 
To see full dependecy tree use:
```
$ mvn dependency:tree
```

#### Profiles

The application recognizes 2 distinct profiles:

* dev: This is the active by default profile, used for development
* production: To be activated when in production


The application ships with migrations that will dynamically update the MongoDB database, using these 2 profiles as reference.

The migrations will ensure that there will be a **seed** ADMIN user that makes the application ready to be used.

Please check [ChangeLogV1.java](https://github.com/cadu-goncalves/user-mng/blob/master/src/main/java/com/creativedrive/user/persistence/changelogs/ChangeLogV1.java) for more details about the credentials for these **seed** users in both profiles.


## Build

The project ships with a Makefile that can be used as follows:

###### Start Test DB
Start MongoDB container
```
$ make start-testdb
```

###### Stop Test DB
Stop and destroys MongoDB container
```
$ make stop-testdb
```

###### Test
Perform all tests (auto start/stop test DB)
```
$ make test
```

###### Build
Build application fat JAR
```
$ make build
```

###### Run
Provides local application instance ready to use
```
$ make run-local
```

## Application Properties

Spring Boot applications rely on auto configuration capabilities provided by the framework. 

Most of these configurations can be overridden using enviroment attributes or simply providing an specific `application.properties` file. 
    
Here are the most relevant configurations that the application uses:
    
  Name        | Description | Default
 -------------|-------------|------
| spring.profiles.active | Application active profiles | dev
| server.port | HTTP server port | 8080
| app.config.threads.min | Service thread pool minimal size, limited to 500 | 10
| app.config.threads.max | Service thread pool maximal size, limited to 500 | 30
| spring.data.mongodb.database|MongoDB database name|users
| spring.data.mongodb.host|MongoDB database host|localhost
| spring.data.mongodb.port|MongoDB database port|27017

When running with Profile `production`, is recommended to change these configurations to match specific environment requirements.

To do that simply create a file named `application.properties` **in the same path of the application JAR file**. Do not forget to change the entry `spring.profiles.active`in this file to `production`.

Please note that you just need to include in this file the configurations you need to change/add, since Spring Boot will merge all configurations together before running the application.

For details for external application configurations please check Spring Boot docs [Application Property Files](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files)

## API Documentation

To access the API documentation run the application and go to the following URL:

(suppose running locally):
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

The application provides inline clients, but remember you be asked for user/password to send requests.