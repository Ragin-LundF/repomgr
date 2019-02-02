# Repository Manager #
This tool is build to manage versions from CI/CD processes.

It can be used at the build-pipeline to store artifact versions and additional information to a central database.
There are possibilities to differ between branch versions and projects.


# Admin User #

For a simple start an user will be imported at the first startup.

| Username | Password | UserId |
|---|---|---|
| admin | password | 5c6a1223-076b-4bc0-b0b7-20b0da0e23fd |

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
