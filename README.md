![Build](https://github.com/InseeFrLab/Sabiane-Data/actions/workflows/release.yml/badge.svg)

# Sabiane-Data
API for the management of questionnaires and organizational data for Computer-Assisted Personal Interviewing (CAPI) Data Collection 
REST API for communication with Queen-Back-Office and Pearl-Jam-Bak-Office.

## Status

PoC: experimentation to refactor a batch approach for Sabiane data management

## Quick start :

### With docker :

- `docker run -p 8080:8080 -t inseefrlab/sabiane-data`

To override environments variables you can do :

- `docker run -p 80:80 -e FR_INSEE.SABIANEDATA.API.HOST=http://override.value.com -t inseefr/queen`

### With Maven - Requirements
For building and running the application you need:
- [JDK 11](https://jdk.java.net/archive/)
- Maven 3  

### With Maven - Install and execute unit tests
Use the maven clean and maven install 
```shell
mvn clean install
```  

## With Maven - Running the application locally
Use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:  
```shell
mvn spring-boot:run
```  

## Application Accesses locally
To access to swagger-ui, use this url : [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)  
To access to h2 console, use this url : [http://localhost:8080/api/h2-console](http://localhost:8080/api/h2-console)  
 
## Deploy application on Tomcat server
### 1. Package the application
Use the [Spring Boot Maven plugin]  (https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:  
```shell
mvn clean package
```  
The war will be generate in `/target` repository  

### 2. Install tomcat and deploy war
To deploy the war file in Tomcat, you need to : 
Download Apache Tomcat and unpackage it into a tomcat folder  
Copy your WAR file from target/ to the tomcat/webapps/ folder  

### 3. Tomcat config
Before to startup the tomcat server, some configurations are needed : 
 

#### External Properties file
Create sabianedata.properties near war file and complete the following properties:  
```shell  
#Profile configuration

# Security : "keycloak" or "none"
fr.insee.sabianedata.security=none

#############  Swagger host ############# 
fr.insee.sabianedata.api.scheme=http
fr.insee.sabianedata.api.host=localhost:8080

############# Logging ############# 
logging.config=${catalina.base}/webapps/log4j2.xml

...