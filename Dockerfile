FROM adoptopenjdk/openjdk11:latest

# Env variables
ENV REPO_MAN_DB_HOST repoman_db
ENV REPO_MAN_DB_PORT 5432
ENV REPO_MAN_DB_USER repomgr
ENV REPO_MAN_DB_PASS repomgr
ENV REPO_MAN_DB_DATABASE repomgr

# copy the application
ADD build/libs/*.jar /app.jar

# expose the port
EXPOSE 9090

# start the appication
ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", \
            "/app.jar", \
            "--repoadmin_db_host=${REPO_MAN_DB_HOST}", \
            "--repoadmin_db_port=${REPO_MAN_DB_PORT}", \
            "--repoadmin_db_database=${REPO_MAN_DB_DATABASE}", \
            "--repoadmin_db_username=${REPO_MAN_DB_USER}", \
            "--repoadmin_db_password=${REPO_MAN_DB_PASS}" \
]
