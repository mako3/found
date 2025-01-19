# found
Simple viewer of archived chat log specialized for Google Chat.

## Features 

## Run found with a Database Container

> [!NOTE]
> Docker runtime environment is necessary. 

1. Create network

```
$ docker network create ${NETWORK}
```

Please replace ${NETWORK} like 'my_network'. 

2. Start a database (Paradedb) container

```
$ docker run \
  --name paradedb \
  --network ${NETWORK} \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=mypassword \
  -e POSTGRES_DB=mydatabase \
  -v paradedb_data:/var/lib/postgresql/data/ \
  -p 5432:5432 \
  -d \
  paradedb/paradedb:v0.14.0
```
Please replace placeholders. 

| Placeholder | Example |
|---|---|
| ${NETWORK} | my_network |
| myuser | postgres |
| mypassword | admin |
| mydatabase | postgres | 



> [!NOTE]
> Pleae check https://docs.paradedb.com/documentation/getting-started/install for detail.


3. Start found

```
$ docker run \
  --name found \
  --network ${NETWORK} \
  -p 8080:8080 \
  -e POSTGRES_HOST=${POSTGRES_HOST} \ 
  -e POSTGRES_USER=${POSTGRES_USER} \ 
  -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
  -e POSTGRES_DB=${POSTGRES_DB} \
  -d \
  ghcr.io/mako3/found:latest
```

Please replace placeholders. 

| Placeholder | Example |
|---|---|
| ${NETWORK} | my_network |
| ${DATASOURCE_HOST} | paradedb |
| ${POSTGRES_USER} | postgres_user |
| ${POSTGRES_PASSWORD} |  admin |
| ${POSTGRES_DB} |  postgres |

4. Open Web browser

> http://localhost:8080/found

You can login with root/root. 


## Stop / Restart

use 'docker container stop' comand
use 'docker container start' command or 'docker container restart' for restart.


## Build & Run found in your local PC

> [!NOTE]
> Required Java Version 17


1. Start a database (Paradedb) container

```
$ docker 
```

2. Pull sourcode 

```
$ git clone 
```

3. Fix application.yaml


4. Build the code with Maven

```
$ mvn
```

5. Run found

```
$ java 
```


## License