package com.stosik.kafka.consumer.avro

import com.sksamuel.avro4k.Avro
import com.stosik.kafka.models.avro.TransactionCreatedAvroEvent
import com.stosik.kafka.models.consumer.kafkaConsumer
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

@Component
internal class TransactionKafkaAvroConsumer(
    @Value("\${schema-registry-url}") schemaRegistry: String
) {

    private val consumer = kafkaConsumer<String, GenericRecord> {
        configuration {
            bootstrapServers = listOf("http://localhost:9092")
            keyDeserializer = StringDeserializer::class.java
            valueDeserializer = KafkaAvroDeserializer::class.java
            groupId = "tms-dashboard-api-avro"
            schemaRegistryUrl = schemaRegistry
        }
        topics {
            topic(TRANSACTION_CREATED_TOPIC)
        }
    }

    @Scheduled(fixedRate = 2000)
    fun consume() {
        val events = consumer
            .poll(Duration.ofSeconds(1))
            .map { Avro.default.fromRecord(TransactionCreatedAvroEvent.serializer(), it.value()) }

        for (event in events) {
            println("Received Avro event: $event")
        }
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.avro.transaction.created"
    }
}