# found
Simple viewer of archived chat log specialized for Google Chat. 

## Features 

1. Message Search
2. Space Search
3. User Management
4. Message/Group Json Upload
5. Role Management

## Run found with a Database Container

> [!NOTE]
> Docker runtime environment is necessary. 

1. Create network

```
$ docker network create ${NETWORK}
```

Please replace ${NETWORK} like 'my_network' so that AP container can access to DB container. 

2. Start a database (Paradedb) container

```
$ docker run \
  --network ${NETWORK} \
  --name ${POSTGRES_HOST} \
  -e POSTGRES_USER=${POSTGRES_USER} \
  -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
  -e POSTGRES_DB=${POSTGRES_DB} \
  -v paradedb_data:/var/lib/postgresql/data/ \
  -p 5432:5432 \
  -d \
  paradedb/paradedb:v0.15.0
```
Please replace placeholders. 

| Placeholder | Example |
|---|---|
| ${NETWORK} | my_network |
| ${POSTGRES_HOST} | paradedb | 
| ${POSTGRES_USER} | postgres |
| ${POSTGRES_PASSWORD} | admin |
| ${POSTGRES_DB} | postgres | 

> [!NOTE]
> Please check https://docs.paradedb.com/documentation/getting-started/install for more details of ParadeDB.

3. Start found

```
$ docker run \
  --name found \
  --network ${NETWORK} \
  -p 80:8080 \
  -e POSTGRES_HOST=${POSTGRES_HOST} \ 
  -e POSTGRES_USER=${POSTGRES_USER} \ 
  -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
  -e POSTGRES_DB=${POSTGRES_DB} \
  -e CONTEXT_PATH=${CONTEXT_PATH} \
  -e SESSION_TIMEOUT=${SESSION_TIMEOUT} \
  -d \
  ghcr.io/mako3/found:latest
```

Please replace placeholders. 

| Placeholder | Example | Default | 
|---|---|---|
| ${NETWORK} | my_network ||
| ${DATASOURCE_HOST} | paradedb ||
| ${POSTGRES_USER} | postgres ||
| ${POSTGRES_PASSWORD} |  admin ||
| ${POSTGRES_DB} |  postgres ||
| ${CONTEXT_PATH} | /found | /found |
| ${SESSION_TIMEOUT} | 86400 | 86400 | 

4. Open Web browser

> http://localhost:80/found

You can login with root/admin. 


## Stop / Restart

* use 'docker container stop' comand
* use 'docker container start' command or 'docker container restart' for restart.


## Build & Run found in your local PC

> [!NOTE]
> Java 17 is required. Please insall JDK.


1. Start a database (Paradedb) container

```
$ docker run \
  --name paradedb \
  -e POSTGRES_USER=${POSTGRES_USER} \
  -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
  -e POSTGRES_DB=${POSTGRES_DB} \
  -v paradedb_data:/var/lib/postgresql/data/ \
  -p 5432:5432 \
  -d \
  paradedb/paradedb:v0.15.0
```
Please replace placeholders. 

| Placeholder | Example |
|---|---|
| ${POSTGRES_USER} | postgres |
| ${POSTGRES_PASSWORD} | admin |
| ${POSTGRES_DB} | postgres | 

> [!NOTE]
> As for ParadeDB, please check https://docs.paradedb.com/documentation/getting-started/install for detail.

2. Pull sourcode of found

```
$ git clone https://github.com/mako3/found.git
```

3. Fix application.yaml

```
spring:
    datasource:
        url: jdbc:postgresql://localhost:5432/postgres
        username: postgres
        password: admin
```

Please specify credentials of ParadeDB as you specified in former step.

4. Build the code with Maven

Set current directly into downloaded found directly and execute following command. 

```
$ mvn package -f /pom.xml
```

5. Run found

```
$ java -jar target/found-0.0.1-SNAPSHOT.jar
```

6. Open Web browser

> http://localhost:8080/found

You can login with root/admin. 

## License

This project is licensed under the MIT License.

This project uses the following third-party libraries:

- [Spring Framework](https://spring.io/projects/spring-framework), licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- [Thyemeleaf](https://www.thymeleaf.org/),  licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- [Lombok](https://projectlombok.org/), llicensed under the [MIT License](https://opensource.org/license/mit)
- [Jackson](https://github.com/FasterXML/jackson-core), licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- [Apache Maven](https://maven.apache.org/), licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/), licensed under the [2-Clause BSD License](https://opensource.org/license/BSD-2-Clause)