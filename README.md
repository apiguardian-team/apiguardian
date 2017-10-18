# @API Guardian

Library that provides the `@API` annotation that is used to annotate public types, methods, constructors, and fields within a framework or application in order to publish their status and level of stability and to indicate how they are intended to be used by consumers of the API.

## Continuous Integration Builds

| CI Server | OS      | Status | Description |
| --------- | ------- | ------ | ----------- |
| Jenkins   | Linux   | [![Build Status](https://junit.ci.cloudbees.com/buildStatus/icon?job=API_Guardian)](https://junit.ci.cloudbees.com/job/API_Guardian) | Official CI build server for @API Guardian |
| Travis CI | Linux   | [![Travis CI build status](https://travis-ci.org/apiguardian-team/apiguardian.svg?branch=master)](https://travis-ci.org/apiguardian-team/apiguardian) | Used to perform quick checks on submitted pull requests |

## How to use it

The @API Guardian library is deployed to maven central. You can simply add it as a dependency:

### Apache Maven
```xml
<dependency>
    <groupId>org.apiguardian</groupId>
    <artifactId>apiguardian-api</artifactId>
    <version>1.0.0</version>
</dependency>
```
### Gradle/Grails
```
compile 'org.apiguardian:apiguardian-api:1.0.0'
```
