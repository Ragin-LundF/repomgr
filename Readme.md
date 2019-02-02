# Repository Manager #

[![Build Status](https://dev.azure.com/ragin666/RepoMgr/_apis/build/status/RepoMgr-Gradle-CI?branchName=master&jobName=RepoMgr%20Build)](https://dev.azure.com/ragin666/RepoMgr/_build/latest?definitionId=6&branchName=master)

This tool is build to manage versions from CI/CD processes.

It can be used at the build-pipeline to store artifact versions and additional information to a central database.
There are possibilities to differ between branch versions and projects.

# RepoMgr UI #

A simple UI is included to see the available packages.
[http://localhost:9090/public/index.html](http://localhost:9090/public/index.html)

At the moment this UI is pre-build as static bundle and index.html!

If something is changed at the React code, the bundle.js must be rebuild.

```bash
cd src/main/resources/ui
npm install
npx webpack
```

# Admin User #

For a simple start an user will be imported at the first startup.

| Username | Password | UserId |
|---|---|---|
| admin | password | 5c6a1223-076b-4bc0-b0b7-20b0da0e23fd |

# API #

A swagger file is available under [`src/main/resources/static/swagger.yaml`](src/main/resources/static/swagger.yaml).

There is also a [Postman](https://www.getpostman.com) collection available for some small tool support. You can find it under [`etc/postman/`](etc/postman).

## Security ##

Each endpoint is secured with a JWT token, with the exception of the `/authentication/generate-token` endpoint.

This endpoint needs credentials and creates a new token for the user.

Example of a JWT-token content:

```json
{
  "sub": "admin",
  "scopes": [
    {
      "authority": "ROLE_ADMIN"
    }
  ],
  "iss": "RepoManager",
  "iat": 1549061618,
  "exp": 1549065229
}
```

## User API ##

The Repository Manager has a simple API for managing users (create, update password, delete) and a simple role-system (ADMIN, USER) to separate users and/or tokens for every project.

## Authentication API ##

This API offers to generate a token with credentials. With this token a pipeline can use the repository API.

## Repository API ##

The repository API is for storing artifact information to the system.

# Docker support #

## Docker image ##
The existing `Dockerfile` builds the application with an alpine linux and OpenJDK 11.

### Environment variables ###
The image needs the following environment variables for application configuration.

| Variable | Description | Default value |
|---|---|---|
| REPO_MAN_DB_HOST | Host or IP of the database | localhost |
| REPO_MAN_DB_PORT | Port of the database | 5432 |
| REPO_MAN_DB_USER | The database username | repomgr |
| REPO_MAN_DB_PASS | The database user password | repomgr |
| REPO_MAN_DB_DATABASE | The database | repomgr |
| REPO_MAN_DB_JDBC | JDBC server name (jdbc://REPO_MAN_DB_JDBC:/...) | postgres |
| REPO_MAN_DB_DRIVER | JDBC driver class | org.postgresql.Driver |

### Building an image ###

```bash
docker build -t repo-mgr .
```

### Running the image ###

After the build, the image can be started like:
```bash
docker run -d -p 9090:9090 --env REPO_MAN_DB_JDBC=postgres --env REPO_MAN_DB_DRIVER=org.postgresql.Driver --env REPO_MAN_DB_HOST=localhost --env REPO_MAN_DB_PORT=5432 --env REPO_MAN_DB_USER=repomgr --env REPO_MAN_DB_PASS=repomgr --env REPO_MAN_DB_DATABASE=repomgr --name=RepoManager repo-mgr 
```

Stop the image with:
```bash
docker stop RepoManager
```

## Using docker-compose ##

The pre-configured `docker-compose.yaml` builds the server image and a postgres database.

Starting the complete environment:

```bash
docker-compose up -d
```

Stopping the environment:

```bash
docker-compose down
```
