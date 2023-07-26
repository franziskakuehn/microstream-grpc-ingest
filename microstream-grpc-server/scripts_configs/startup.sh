#!/usr/bin/env bash

set -euo pipefail

source scripts_configs/helpers.sh

docker-compose --project-name microstream-grpc-network down -v

docker-compose --project-name microstream-grpc-network up -d

MAX_WAIT=180
echo "Waiting up to $MAX_WAIT seconds for connect to start"
retry $MAX_WAIT check_connect_up connect || exit 1
sleep 2 # give kafka connect an extra moment to fully mature
echo "connect has started!"

curl -X POST -H "Content-Type: application/json" --data @scripts_configs/connector_pageviews.config http://localhost:8083/connectors

#curl -X POST -H "Content-Type: application/json" --data @scripts_configs/connector_users.config http://localhost:8083/connectors