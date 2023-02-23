# Kafka producer-consumer POC with usage of json, avro and protobuf protocols

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