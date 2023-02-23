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
import java.time.Duration

@Component
internal class TransactionKafkaProtobufConsumer(
    @Value("\${schema-registry-url}") schemaRegistryUrl: String
) {

    private val consumer: KafkaConsumer<String, TransactionCreatedProtobufEvent> by lazy {
        val consumerProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "http://localhost:9092",
            AUTO_OFFSET_RESET_CONFIG to "earliest",
            KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java,
            GROUP_ID_CONFIG to "tms-dashboard-api",
            SECURITY_PROTOCOL_CONFIG to "PLAINTEXT",
            "schema.registry.url" to schemaRegistryUrl
        )

        KafkaConsumer<String, TransactionCreatedProtobufEvent>(consumerProps).also {
            it.subscribe(
                listOf(
                    TRANSACTION_CREATED_TOPIC
                )
            )
        }
    }

    @Scheduled(fixedDelay = 1000)
    fun consume() {
        while (true) {
            val events = consumer
                .poll(Duration.ofSeconds(1))
                .map { it.value() }

            for (event in events) {
                println("Received Proto event: $event")
            }
        }
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.proto.transaction.created"
    }
}