#!/bin/bash

docker-compose -f src/main/docker/keycloak/keycloak.yml up -d
# docker logs -f spring-keycloak