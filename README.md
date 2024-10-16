# @API Guardian

[![CI Status](https://github.com/apiguardian-team/apiguardian/workflows/CI/badge.svg)](https://github.com/apiguardian-team/apiguardian/actions)

Library that provides the `@API` annotation that is used to annotate public types, methods, constructors, and fields within a framework or application in order to publish their status and level of stability and to indicate how they are intended to be used by consumers of the API.

## How to use it

The @API Guardian library is deployed to Maven Central. To avoid compile-time warnings, you need to declare it as a _transitive_ compile-time dependency.

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

Using `compileOnlyApi` will include the library on the compile classpath of downstream projects but not their runtime classpath. If you want downstream projects to be able to use the `@API` annotation at runtime, you should declare it as `api` instead:

```gradle
dependencies {    
    api("org.apiguardian:apiguardian-api:1.1.2")
}
```

### Java Platform Module System

```java
module org.example {
    requires static transitive org.apiguardian.api;
}
```

Using `static` will only include the library on the module path of downstream projects at compile time but not at runtime. If you want downstream projects to be able to use the `@API` annotation at runtime, you should declare it without `static` instead:

```java
module org.example {
    requires transitive org.apiguardian.api;
}
```
