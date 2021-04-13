# Simple_JDBC_ONOS_App

This is a simple example ONOS App for connecting to external database (e.g. MySQL).

### Prerequisite

* MySQL database
* ONOS v2.5.1
* JDK 11
* Bazel v3.7.2

## MySQL Connector/J

[Download Driver](https://dev.mysql.com/downloads/connector/j/)  
[Source Code](https://github.com/mysql/mysql-connector-j.git)  

This is a driver that implements the JDBC (***J***ava ***D***ata***B***ase ***C***onnectivity) API.
MySQL Connector/J 8.0 is a JDBC **Type 4** driver.
The Type 4 designation means that the driver is a **pure** Java implementation of the MySQL protocol and does not rely on the MySQL client libraries.
[JDBC Basics](https://github.com/JustinSDK/JavaSE6Tutorial/blob/master/docs/CH20.md?fbclid=IwAR204l4sGneQFpLbgnraa3aqfIC4WS-Q19JrDJ0_GOrOBrDVPeO9RL-fUOY)  

* Add Maven Dependency   
```xml
<!--https://mvnrepository.com/artifact/mysql/mysql-connector-java-->
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>${version}</version>
</dependency>
```
Note: later version of v8.0.0 use `com.mysql.cj.jdbc.Driver` driver class instead of `com.mysql.jdbc.Driver`  

## About OSGi Bundle

**OSGi** is a Java framework for developing and deploying modular software programs and libraries.
Each bundle is a tightly coupled, dynamically loadable collection of classes, jars, and configuration files that explicitly declare their external dependencies (if any).
**Bundles** are normal **JAR** components with extra manifest headers.  

The following steps show how to create bundle for 3rd party dependency:

1. Karaf supports the `wrap:` protocol execution.
[Karaf Developers Guide](https://karaf.apache.org/manual/latest-2.x/developers-guide/creating-bundles.html)  
    In **feature.xml**
    ```xml
    <bundle>wrap:mvn:mysql/mysql-connector-java/${mysqlConnectorJavaVersion}</bundle>
    ```
   
2. How to build OSGi bundles using Maven Bundle Plugin? 
    [Check Here](https://wso2.com/library/tutorials/develop-osgi-bundles-using-maven-bundle-plugin/)  
    In **pom.xml**
    ```xml
    <plugin>
        <groupId>org.apache.felix</groupId>
        <artifactId>maven-bundle-plugin</artifactId>
        <version>3.5.0</version>
        <extensions>true</extensions>
        <configuration>
            <instructions>
                <Bundle-Name>${onos.app.origin} ${onos.app.title}</Bundle-Name>
                <Bundle-SymbolicName>${project.groupId}.${project.artifactId}</Bundle-SymbolicName>
                <Bundle-Version>${project.version}</Bundle-Version>
                <Import-Package>
                    com.mysql.cj.jdbc,
                    org.slf4j
                </Import-Package>
            </instructions>
        </configuration>
    </plugin>
    ```
    
    `<Import-Package>` specifies dependent packages exposed from other bundle.    


3. Use ONOS (Karaf) CLI to check bundle information.
[ONOS Wiki](https://wiki.onosproject.org/display/ONOS/The+ONOS+CLI)  
    ```shell
    $ onos localhost
    onos@root > bundle:list               # list the bundles that comprise the running ONOS instance
    ...
    212 │ Active │  80 │ 8.0.16             │ Oracle Corporation's JDBC and XDevAPI Driver for MySQL
    213 │ Active │  80 │ 1.0.0.SNAPSHOT     │ Winlab, NCTU MySQL Test APP
    
    onos@root > bundle:headers 213        # display bundle information
    
    Winlab, NCTU MySQL Test APP (213)
    ---------------------------------
    Bnd-LastModified = 1618252776079
    Build-Jdk = 11.0.10
    Built-By = onos
    ...
    Bundle-Name = Winlab, NCTU MySQL Test APP
    Bundle-SymbolicName = nctu.winlab.mysql-test
    Bundle-Version = 1.0.0.SNAPSHOT
    ...
    Export-Package =
            nctu.winlab.mysql;version=1.0.0.SNAPSHOT
    Import-Package =
            com.mysql.cj.jdbc;version="[8.0,9)",          # the package specified at last step
            org.slf4j;version="[1.7,2)"
    ```
   

## Class Loader

```java
try {
    Class.forName("com.mysql.jdbc.Driver");
}
catch(ClassNotFoundException e) {
    System.out.println("Can not find the driver!");
}
```

> With ONOS being housed in an OSGi framework/container, it is important to remember that there are multitudes of class-loaders involved.
[Ref.](https://groups.google.com/a/onosproject.org/g/onos-dev/c/ft8schbe74g/m/PyEtaVV6CwAJ)

This will throw `ClassNotFoundException` because the current class loader couldn't find the specified driver class.  


So I new a nameless object. In this way, it can properly register driver in the class static clause.
```java
 // Nameless object for executing class static clause. (MySQL Connector/J JDBC driver)
new Driver();
```
