#!/bin/bash

ssh root@109.234.34.73 << END
    cd /root
    docker exec -it postgres sh -c 'TIMESTAMP=$(date "+%s");pg_dump -h "$POSTGRES_PORT_5432_TCP_ADDR" -p "$POSTGRES_PORT_5432_TCP_PORT" -U langdb_user langdb > /var/lib/postgresql/data/dump_${TIMESTAMP}.sql'
    ./runner.sh
END