# @API Guardian

[![CI Status](https://github.com/apiguardian-team/apiguardian/workflows/CI/badge.svg)](https://github.com/apiguardian-team/apiguardian/actions)

Library that provides the `@API` annotation that is used to annotate public types, methods, constructors, and fields within a framework or application in order to publish their status and level of stability and to indicate how they are intended to be used by consumers of the API.

## How to use it

The @API Guardian library is deployed to Maven Central. You can simply add it as a dependency:

### Apache Maven
```xml
<dependency>
    <groupId>org.apiguardian</groupId>
    <artifactId>apiguardian-api</artifactId>
    <version>1.1.2</version>
</dependency>
```

### Gradle

```gradle
repositories {
    mavenCentral()
}
dependencies {    
    compileOnlyApi("org.apiguardian:apiguardian-api:1.1.2")
}
```
