# Kafka Showcase

The showcase demonstrates Apache Kafka combined with MicroProfile Reactive Messaging, MicroProfile OpenTracing and other techniques. 

Software requirements to run the samples are `maven`, `openjdk-1.8` (or any other 1.8 JDK) and `docker`.

## How to run

Before running the application it needs to be compiled and packaged using Maven. It creates the required docker images and can be run together with Apache Kafka via `docker-compose`:

```shell script
$ mvn clean package
$ docker-compose up
```

When changing code you must re-run the package process and start docker-compose with the additional build parameter to
ensure that both the application and the Docker image is up-to-date:
```shell script
$ mvn clean package
$ docker-compose up --build
```

### Resolving issues

Sometimes it may happen that the containers did not stop as expected when trying to stop the pipeline early. This may
result in running containers although they should have been stopped and removed. To detect them you need to check
Docker:

```shell script
$ docker ps -a | grep <id of the container>
```

If there are containers remaining although the application has been stopped you can remove them:

````shell script
$ docker rm <ids of the containers>
````

