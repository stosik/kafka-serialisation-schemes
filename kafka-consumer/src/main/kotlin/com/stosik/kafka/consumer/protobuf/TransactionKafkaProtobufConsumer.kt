package com.stosik.kafka.consumer.protobuf

import com.stosik.kafka.models.consumer.kafkaConsumer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pl.stosik.TransactionCreatedEventOuterClass.TransactionCreatedEvent
import java.time.Duration
import java.util.*


@Component
internal class TransactionKafkaProtobufConsumer(
    @Value("\${schema-registry-url}") schemaRegistry: String
) {

    private val consumer = kafkaConsumer<String, TransactionCreatedEvent> {
        configuration {
            bootstrapServers = listOf("http://localhost:9092")
            keyDeserializer = StringDeserializer::class.java
            valueDeserializer = KafkaProtobufDeserializer::class.java
            groupId = "tms-dashboard-api-proto"
            schemaRegistryUrl = schemaRegistry
            protobufClass = TransactionCreatedEvent::class.java
        }
        topics {
            topic(TRANSACTION_CREATED_TOPIC)
        }
    }

    @Scheduled(fixedRate = 2000)
    fun consume() {
        val events = consumer
            .poll(Duration.ofSeconds(1))
            .map { it.value() }

        for (event in events) {
            println("Received Proto event: $event")
        }
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.proto.transaction.created"
    }
}