# App Web Hello sample

Copy `target/appwebhello-1.0.0.war` to `<tomee>/webapps` dir.

Open in browser: `localhost:8080/appwebhello-1.0.0/appwebhello/hello`

Notes:
Add property to `conf/system.properties` property file:
```properties
tomee.jpa.factory.lazy=true
```

