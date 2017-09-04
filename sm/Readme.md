# SM Engine - Netflix Zuul

## Overview
This is parent project which has two modules
1. ldap - library
2. proxy - Spring boot application which sets up Zuul proxy. 

Proxy module is an executable module which can be deployed. But it requires ldap dependency so it has to build and installed via Maven before building the proxy.

## Configuration
Refer the respective module's Readme.md to configure.

## Prerequisite

1. JDK 1.8
2. Maven
3. Git
4. Heroku CLI

## Local deployment

Open the command prompt and execute the below commands

```
mvn clean install
mvn package && java -jar proxy/target/proxy-1.0.0.jar
```

## Heroku deployment steps
0. Steps 1,2,3 are optional if it's performed already.

1. Create Heroku account from [here](https://signup.heroku.com/) and get your account credentials.

2. Install the Heroku CLI tool. Detailed instruction on how to install for various operating system is available [here](https://devcenter.heroku.com/articles/heroku-cli)
3. Download the git client from [here](https://git-scm.com/downloads) according to the operating system and install the same.

4. Open the command prompt and login to Heroku by entering following command followed by Heroku user id and password.
```
heroku login
```
5. Create an app on Heroku, which prepares Heroku to receive your source code.
```
heroku create
```
Heroku generates a random app name and corresponding git repo. The details of the app name and git repo URL will be printed after executing the above command.

6. Initialize and commit the project in git
```
git init
git add *
git commit -m "Commit command"
```
7. Push the project into heroku git repo created in step 5.
```
git remote add sm <git repo URL create at step 5>
git push -u sm master
```
8. The application is now deployed. Start the instance
```
heroku ps:scale web=1 --remote sm
```
9. View the logs using the tail command
```
heroku logs --tail --remote sm
```
