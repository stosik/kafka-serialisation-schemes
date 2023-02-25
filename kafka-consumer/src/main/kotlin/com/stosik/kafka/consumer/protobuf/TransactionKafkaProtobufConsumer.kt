package com.stosik.kafka.consumer.protobuf

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pl.stosik.TransactionCreatedEventOuterClass.TransactionCreatedEvent
import java.time.Duration

@Component
internal class TransactionKafkaProtobufConsumer(
    @Value("\${schema-registry-url}") schemaRegistryUrl: String
) {

    private val consumerProps = mapOf(
        BOOTSTRAP_SERVERS_CONFIG to "http://localhost:9092",
        AUTO_OFFSET_RESET_CONFIG to "earliest",
        KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java,
        GROUP_ID_CONFIG to "tms-dashboard-api-proto",
        SECURITY_PROTOCOL_CONFIG to "PLAINTEXT",
        "schema.registry.url" to schemaRegistryUrl
    )

    private val consumer = KafkaConsumer<String, TransactionCreatedEvent>(consumerProps).also {
        it.subscribe(listOf(TRANSACTION_CREATED_TOPIC))
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