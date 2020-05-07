## AeroUI

Simple web app for aerospike build in scala with Play.

### Quickstart
Run the following command in your terminal :
```
docker run -d --rm -p 9000:9000 jeanwisser/aero-ui:latest
```

The application is now accessible at `http://localhost:9000`.

### Secure start
By default, play is configured to use a public key accessible by everyone.
For production environment, it is recommended to override the default with your own using the environment variable `APPLICATION_SECRET`:
```
docker run -e APPLICATION_SECRET`="mysecretkey" -d --rm -p 9000:9000 jeanwisser/aero-ui:latest
```
For more details: https://www.playframework.com/documentation/2.8.x/ApplicationSecret