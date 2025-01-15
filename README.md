# found
Simple viewer of archived chat log specialized for Google Chat.

## Features 


## Run found with a Database Container

1. Create network

```
$ docker network create my_network
```

You can define 'my_network' freely.


2. Start a database (Paradedb) container

```
$ docker run \
  --name paradedb \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=mypassword \
  -e POSTGRES_DB=mydatabase \
  -v paradedb_data:/var/lib/postgresql/data/ \
  -p 5432:5432 \
  -d \
  paradedb/paradedb:v0.14.0
```

> [!NOTE]
> Pleae check https://docs.paradedb.com/documentation/getting-started/install for detail.


3. Start found

```
$ docker run \
  --name XXX \
  --network ${NETWORK} \
  -e POSTGRES_USER=${POSTGRES_USER} \ 
  -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
  -d \
  ghcr.io/mako3/found:latest
```

## Stop / Restart

use 'docker container stop' comand
use 'docker container start' command or 'docker container restart' for restart.


## Build & Run found in your local PC

1. Start a database (Paradedb) container

```
$ docker 
```

2. Pull sourcode 

```
$ git
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

Required Java Version 17

## License