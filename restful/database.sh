#!/bin/bash

eval "$(docker-machine env default)"
docker rm -f postgres ; docker run --name postgres -d -e POSTGRES_DB=langdb -e POSTGRES_USER=user -e POSTGRES_PASSWORD=pass -p 5432:5432 postgres
