#!/bin/bash



function check_docker() {
  if ! docker ps -q &>/dev/null; then
    echo "This example requires Docker but it doesn't appear to be running.  Please start Docker and try again."
    exit 1
  fi

  return 0
}


check_ksqldb_up() {
  containerName=$1

  FOUND=$(docker-compose logs $containerName | grep "API server started")
  if [ -z "$FOUND" ]; then
    return 1
  fi
  return 0
}

check_schemaregistry_up() {
  containerName=$1

  FOUND=$(docker-compose logs $containerName | grep "Server started, listening for requests...")
  if [ -z "$FOUND" ]; then
    return 1
  fi
  return 0
}


check_connect_up() {
  containerName=$1

  FOUND=$(docker logs $containerName | grep "Kafka Connect started")
  if [ -z "$FOUND" ]; then
    return 1
  fi
  return 0
}

check_service_up() {
	containerName=$1
	port=$2
	while ! nc -z "$containerName" $port;
	do
		echo "waiting for getting $containerName ready"
		sleep 30
	done
	echo "Connected"
}

check_topic_exists() {
  containerName=$1
  brokerConn=$2
  topic=$3

  docker-compose exec $containerName kafka-topics --zookeeper $brokerConn --describe --topic $topic >/dev/null
  return $?
}

check_topic_ExistAndhasContent() {
  brokerConn=$1
  topic=$2

  CONTENT=$(docker run --name tester --tty --network complex-objects_default confluentinc/cp-kafkacat kafkacat -b $brokerConn -t $topic -C -e | wc -l)
  if [ $CONTENT -lt 2 ]; then
    return 1
  else
    return 0
    echo "TOPIC successfully filled"
  fi

  docker rm tester

}


import_topic_json_to_avro(){
  INPUTDATA=$1
  TOPIC=$2

    cat "$INPUTDATA-avro.json" | \
    kafka-avro-console-producer --topic $TOPIC --bootstrap-server broker:29092 \
        --property "schema.registry.url=http://schema-registry:8081" \
        --property key.schema="$(< $INPUTDATA.keyschema)" \
        --property value.schema="$(sed '/namespace/d' $INPUTDATA.valueschema)" \
        --property parse.key=true  --property key.separator=$'\x1c'
}

import_topic_json_to_avro_docker(){
  INPUTDATA=$1
  TOPIC=$2

  docker exec -e INPUTDATA=$INPUTDATA -e TOPIC=$TOPIC schema-registry bash -c \
      'cat $INPUTDATA-avro.ndjson | \
      kafka-avro-console-producer --topic $TOPIC --broker-list broker:29092 \
          --property "schema.registry.url=http://schema-registry:8081" \
          --property key.schema="$(< $INPUTDATA-key.avsc)" \
          --property value.schema="$(sed '/namespace/d' $INPUTDATA-value.avsc)" \
          --property parse.key=true  --property key.separator=$'\'\\x1c\'''
}

import_topic_json_to_avro_gz(){
  INPUTDATA=$1
  FOLDER=$2
  TOPIC=$3
  ENVIRONMENT=$4

  docker exec -e INPUTDATA=$INPUTDATA -e FOLDER=$FOLDER -e TOPIC=$TOPIC -e ENVIRONMENT=$ENVIRONMENT schema-registry bash -c \
      'cat $INPUTDATA-avro.ndjson.gz | gunzip| \
      kafka-avro-console-producer --topic $TOPIC --bootstrap-server broker:29092 \
          --property "schema.registry.url=http://schema-registry:8081" \
          --property key.schema="$(< /data/$FOLDER/$TOPIC-key.avsc)" \
          --property value.schema="$(sed '/namespace/d' /data/$FOLDER/$TOPIC-value.avsc)" \
          --property parse.key=true  --property key.separator=$'\'\\x1c\'''
}

create_empty_topic_docker(){
  TOPIC=$1

  docker exec -it broker kafka-topics --create \
    --zookeeper zookeeper:2181 \
    --replication-factor 1 \
    --partitions 3 \
    --topic $TOPIC

}

create_empty_topic_with_schema_registering_docker(){
  INPUTPATH=$1
  TOPIC=$2

   docker exec -it schema-registry curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
      --data @"$INPUTPATH-value.avsc" http://localhost:8081/subjects/$TOPIC-value/versions

   docker exec -it schema-registry curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
      --data @"$INPUTPATH-key.avsc" http://localhost:8081/subjects/$TOPIC-key/versions

   create_empty_topic_docker $TOPIC

}

retry() {
    local -r -i max_wait="$1"; shift
    local -r cmd="$@"

    local -i sleep_interval=5
    local -i curr_wait=0

    until $cmd
    do
        if (( curr_wait >= max_wait ))
        then
            echo "ERROR: Failed after $curr_wait seconds. Please troubleshoot and run again."
            return 1
        else
            printf "."
            curr_wait=$((curr_wait+sleep_interval))
            sleep $sleep_interval
        fi
    done
    printf "\n"
}



