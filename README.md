# Kafka producer-consumer POC with usage of json, avro and protobuf protocols

### Description

This projects shows how one can easily incorporate different type of
serialization/deserialization formats for events storage and transportation
in the Apache Kafka. The example is based on following formats:

- JSON
- Avro
- Google Proto buffers

Project is divided into three separate modules:

1. kafka-producer - module consisting of producers, responsible for emitting
   random events triggered by endpoint invocation. Each format has its own
   dedicated endpoint and producing event to dedicated topic
2. kafka-consumer - module consisting of consumers, responsible for consuming
   produced events by producers periodically in a scheduled fashion
3. event-models - module aggregating event's models shared across producers and consumers
   for domain specific integrations.

### How to run:

In order to generate needed models for integrations execute:

```shell
cd event-models/
./gradlew generateProto
./gradlew publishToMavenLocal
cd ..
./gradlew build
```

In order to run needed infrastructure:

```shell
docker-compose up -d

docker exec kafka \
kafka-topics --bootstrap-server kafka:9092 \
--create \
--topic pl.stosik.json.transaction.created

docker exec kafka \
kafka-topics --bootstrap-server kafka:9092 \
--create \
--topic pl.stosik.avro.transaction.created

docker exec kafka \
kafka-topics --bootstrap-server kafka:9092 \
--create \
--topic pl.stosik.proto.transaction.created
```

```shell
docker exec kafka \                                                                                                                                                   ✔
kafka-log-dirs --bootstrap-server kafka:9092 \
--describe \
--topic-list pl.stosik.json.transaction.created

docker exec kafka \                                                                                                                                                   ✔
kafka-log-dirs --bootstrap-server kafka:9092 \
--describe \
--topic-list pl.stosik.proto.transaction.created

docker exec kafka \                                                                                                                                                   ✔
kafka-log-dirs --bootstrap-server kafka:9092 \
--describe \
--topic-list pl.stosik.avro.transaction.created
```

This should set the whole infrastructure ready for testing.
You can check if everything is in place going to:

http://localhost:9000

Now it is time run `ProducerApplication` and `ConsumerApplication` from IDE
and run following http request which will generate random events from producers which will
be consumed on the producer side.

```shell
curl --location 'http://localhost:8081/json/transactions/created' --header 'Accept: application/json'
curl --location 'http://localhost:8081/proto/transactions/created'
curl --location 'http://localhost:8081/avro/transactions/created'
```

### Questions

If you have any questions or something is not clear, feel free to contact me :)