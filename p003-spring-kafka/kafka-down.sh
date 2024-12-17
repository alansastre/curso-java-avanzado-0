#!/bin/bash

docker-compose -f src/main/docker/kafka/docker-kafka.yml down
# docker logs -f broker
# docker rm control-center
# docker rm broker