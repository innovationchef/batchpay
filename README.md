# Batch Processor

### Description
This code is written to do some PoC over the Spring Batch framework. <br>
The application constitutes a few batch jobs and the PoC entails covering batch program features like - 
1. Fault Tolerance
2. Restart capabilities
3. Unit testing various components

### System Requirements
Please use the following minimum version of java and maven
~~~
Java version: 1.8.0_181
Apache Maven 3.6.1
~~~

### How to run
To run the unit/integration test cases before running the application - 
~~~
mvn test
~~~
This is a standard Spring Framework project. To run the project directly - 
~~~
mvn spring-boot:run
~~~
To build a jar and run it -
~~~
mvn clean package
cd target/
java -Dserver.port=8080 -jar batchpay-0.0.1.jar
~~~