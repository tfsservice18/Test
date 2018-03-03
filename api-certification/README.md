# API Certification Service

This service is part of light-portal and it is used to check if the production deployment
meet the security requirements. 

The following things will be checked with server/info endpoint:

- If TLS https is enable and key pair are replaced 
- If http port is disabled
- If JWT token verification and scope verification are enabled
- If metrics is enabled
- If the right version of JDK/JRE is used to start the server
- If the default JWT key is replaced 

As server info is a protected endpoints with JWT and scopes, when this service is accessed
from the light-portal view, an access token is needed to access third party service. 

If you implement the light-portal along with light-oauth2 in your own data center, you can
config the light-portal to access your light-oauth2 server to get the token automatically
in order to access all the services projected by the light-oauth2 provider. 


