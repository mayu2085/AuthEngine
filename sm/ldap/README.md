# SM engine - LDAP Service Library
## Overview
LDAP Service library project expose set off services to Active directory operations including login. This can not be used as a standalone. It's a library which can be added as a dependency to spring projects to perform LDAP operations.

## Prerequisites
* Java8
* Maven3
* Git

## Configuration
Usually, you are not required to modify configurations except ldap.url.
You can find default ldap configurations with **ldap** prefix in `src/main/resources/application.properties`.
You can find docker ldap configurations with **ldap** prefix in `src/main/resources/application-docker.properties`.
You can find example ldap ldif file  in `src/main/resources/test.ldif`.

## Local Deployment

It's a library project it can't be used as standalone. To compile and install the module, following commands can be used.

```
mvn clean install
```