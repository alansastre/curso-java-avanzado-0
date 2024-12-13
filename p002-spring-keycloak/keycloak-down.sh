#!/bin/bash

# Borrando el volumen para borrar el realm de keycloak
# docker-compose -f src/main/docker/keycloak/keycloak.yml down -v

docker-compose -f src/main/docker/keycloak/keycloak.yml down