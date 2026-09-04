# Java API Autotests
API test automation project built with Java, JUnit 5 and REST Assured.

## Tech Stack

- Java 21
- Maven
- JUnit 5
- REST Assured
- AssertJ
- Jackson

## Project Structure

```text
src
└── test
    ├── java
    │   └── qa.dmitriy
    │       ├── api
    │       ├── base
    │       ├── client
    │       ├── config
    │       └── model
    │
    └── resources
        └── application.properties
```

## Running Tests
Run all tests:

```bash
mvn clean test
```

## Environment Configuration
The default API URL is configured in:

```text
src/test/resources/application.properties
```

Example:

```properties
base.url=https://jsonplaceholder.typicode.com
```
The base URL can also be overridden through a Maven system property:

```bash
mvn clean test "-Dbase.url=https://jsonplaceholder.typicode.com"
```

The system property has higher priority than the value from `application.properties`.

## Current Tests
The project currently contains API tests for:

- getting a post by ID
- handling a non-existing post
- basic JUnit/AssertJ assertion example