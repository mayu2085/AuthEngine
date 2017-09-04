# SM Engine

- sm folder has the proxy and ldap modules
- mock folder has the implementation of mock services
- postman folder has the postman collection for testing.

## Heroku deployment url for verification

- Mock service - https://radiant-mesa-38297.herokuapp.com
- Zuul proxy - https://young-crag-88090.herokuapp.com

## Testing

Postman collections are in parent folder of this project.

1. Import the postman environment & collection files.
2. Update the end point URLs in the environment.
3. Environment variable name `MOCK_API` is sample service URL and `ZULL_PROXY` is for ZUUL proxy URL.
5. Execute and verify the details 
6. Username & Password to test is `ben` & `benspassword`.

## Demo video
https://youtu.be/NvXvH2rAvkk