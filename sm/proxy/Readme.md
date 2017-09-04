## SM Proxy Service using Netflix Zuul

### Overview
SM proxy service is implemented using Netflix Zuul. This project sets up the Zuul proxy service and performs token validation, authentication and routing based on the business requirements.

### Configurations

1. Zuul route configuration should be done in `.\src\main\resources\application.properties` files.
Example is configured with two routes `api` and `auth` as shown below
```
zuul.routes.api.url=http://localhost:3000
zuul.routes.auth.url=/
```
`zuul.routes.auth.url` is not required to be changes as login submit functionality is handled by filter.
Localhost and port should be changed to Heroku URL for the sample node application/ local deployment address


2. Configure the Zuul proxy server port in `.\src\main\resources\application.properties` as shown below
```
server.port=8080
```

3. Disable the service discovery for this demo purpose.
```
ribbon.eureka.enabled=false
```

4. Configure the LDAP service properties as mentioned in `ldap` [module](../ldap/Readme.md). You can copy paste the `ldap` module configuration in `.\src\main\resources\application.properties`

5. Configure the user search base directory in `.\src\main\resources\application.properties` under `ldap.userSearchBase`. This will be used for user login authentication.


6. Login page and error page are Configure in the `smproxy.properties` as mentioned below. It's not required to be changed as both are served out of proxy server itself.
```
sm.proxy.error.page=/error
sm.proxy.login.page=/login
```
7. Configure the front end system name, http header provider api host, landing page dealer web service url, api token in `smproxy.properties`.  
```
sm.proxy.front_end_system=PartnerCenter
sm.proxy.header_api_path=https://blooming-spire-46327.herokuapp.com
sm.proxy.landing_page_dealer_url=https://blooming-spire-46327.herokuapp.com/ws
sm.proxy.api_token=17A6C5691F09C95D167DE430F8364545
```

8. Configure the landing page dealer web service wsdl location in `pom.xml` under plugin group id `org.jvnet.jaxb2.maven2` schema url.
```
<plugin>
    <groupId>org.jvnet.jaxb2.maven2</groupId>
    <artifactId>maven-jaxb2-plugin</artifactId>
        ....
        <schemas>
            <schema>
                <url>https://blooming-spire-46327.herokuapp.com/ws/LandingPageDealer.wsdl</url>
            </schema>
        </schemas>
        ....
</plugin>
```

## Local Deployment steps

1. Install the JDK 8. The detailed installation instruction for various platform can be found [here](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
2. Install the Maven latest version. The detailed installation instruction for the various platform can be found [here](https://maven.apache.org/install.html). The Maven software can be downloaded from [here](https://maven.apache.org/download.cgi)
3. Verify the installation by executing the following command
```
java -version
mvn -v
``` 
4. Open the current path in the command prompt and execute the following command. Before performing this, please ensure to execute `mvn clean install` from the parent project. So that ldap module dependency will be available in the local maven repository.
```
mvn package && java -jar target/proxy-1.0.0.jar
```

## Heroku deployment

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

6. Update the `application.properties` file to point the Heroku sample service endpoints.

7. Initialize and commit the project in git
```
git init
git add *
git commit -m "Commit command"
```
8. Push the project into heroku git repo created in step 5.
```
git remote add proxy <git repo URL create at step 5>
git push -u proxy master
```
9. The application is now deployed. Start the instance
```
heroku ps:scale web=1 --remote proxy
```
10. View the logs using the tail command
```
heroku logs --tail --remote proxy
```