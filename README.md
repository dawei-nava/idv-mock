# Mocking services for doc-auth external vendor

This application mocks services like Acuant TrueID etc. The default url is http://localhost:8080.

## Build requirement
It depends on one wiremock extension library which is hosted on github, customized configuraiton is needed for `Maven`.

After installation of maven, customize the setting by following [Github documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry). 
To summarize, make sure the repository is configured in `~/.m2/settings.xml` looks like
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/OWNER/REPOSITORY</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>USERNAME</username>
      <password>TOKEN</password>
    </server>
  </servers>
</settings>
```



## Running the service

### Run from code base
```bash
mvn spring-boot:run
```

### Run from packaged jar

#### Package the application
```bash
mvn package
# this will generate packaged jar file idk-mock-0.0.1-SNAPSHOT.jar in target directory
```
#### Run the jar file
```bash
java -jar idk-mock-xxxx.jar
```

## Test various scenarios

### Acuant TrueID

A json file can be used as document images to trigger response needed.

The specification of the json file looks like the following:

```json
{
  "httpStatus": "500|438|439|440",
  "ognlExpression": "",
  "fixedDelays": 1000
}
```

The `ognlExpression` is used to modify a template response using OGNL language. The OGLN language provides the capability not only to navigate the object graph but also to change it. 


An example to trigger a `2D Barcode Content` alert.
```json
{
  "ognlExpression" : "#this.alerts.{? #this.key=='2D Barcode Content'}[0].result=5, #this.result=5"
}
```

List of `Alert` that you can change:

* "2D Barcode Content",
* "2D Barcode Read",
* "Birth Date Crosscheck",
* "Birth Date Valid",
* "Document Classification",
* "Document Crosscheck Aggregation",
* "Document Expired",
* "Document Number Crosscheck",
* "Expiration Date Crosscheck",
* "Expiration Date Valid",
* "Full Name Crosscheck",
* "Image Tampering Check",
* "Issue Date Crosscheck",
* "Issue Date Valid",
* "Sex Crosscheck",

An example to trigger `440` http status code, which means a IMAGE_SIZE failure (438 IMAGE_LOAD_FAILURE, 439 PIXEL_DEPTH_FALURE).
```json
{
  "httpStatus": 440
}
```
