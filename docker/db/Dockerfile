#execute netsh int ipv4 add excludedportrange protocol=tcp startport=2375 numberofports=1 and reboot 
FROM postgres:latest
LABEL key="aljuly"

ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD passw0rd
ENV POSTGRES_DB AdminDB
# COPY schema.sql /docker-entrypoint-initdb.d/
# COPY operations.sql /docker-entrypoint-initdb.d/