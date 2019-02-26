# Document Generator

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/hmcts/finrem-document-generator-client.svg?branch=master)](https://travis-ci.org/hmcts/div-document-generator-client)
[![codecov](https://codecov.io/gh/hmcts/finrem-document-generator-client/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/div-document-generator-client)

This is a document generation service. This allows to generate documents based on a
given template name and placeholder data in JSON format and will also store the generated document in the
Evidence Management Store.

The service provides a single RESTful endpoint that will generate the document, store it in Evidence Management
Store and return the link to the stored data.

## Getting started

### Prerequisites

- [JDK 8](https://www.oracle.com/java)
- [Docker](https://www.docker.com)


### Building

The project uses [Gradle](https://gradle.org) as a build tool but you don't have to install it locally since there is a
`./gradlew` wrapper script.

To build project please execute the following command:

```bash
    ./gradlew build
```

### Running

First you need to create runnable jar by executing following command:

```bash
    ./gradlew clean build
```

When the lib has been created in `build/lib` directory,
you can run the application by executing following command:

1) Run
```
docker build . -t hmcts/finrem-document-generator-client:latest
```

2) Export docmosis-api-access-key (ask finrem dev members to provide one)
```
export PDF_SERVICE_ACCESS_KEY=<ACTUAL_KEY>
```
3) Run
```bash
    docker-compose up
```

As a result the following container(s) will get created and started:
 - long living container for API application exposing port `4009`

### API documentation

API documentation is provided with Swagger:
 - `http://localhost:4009/swagger-ui.html` - UI to interact with the API resources

## Developing

### Unit tests

To run all unit tests please execute following command:

```bash
    ./gradlew test
```

### Coding style tests

To run all checks (including unit tests) please execute following command:

```bash
    ./gradlew check
```

## Standard API

We follow [RESTful API standards](https://hmcts.github.io/restful-api-standards/).

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
