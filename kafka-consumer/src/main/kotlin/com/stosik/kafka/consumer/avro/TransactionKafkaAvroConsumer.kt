package com.stosik.kafka.consumer.avro

import com.sksamuel.avro4k.Avro
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

@Component
internal class TransactionKafkaAvroConsumer(
    @Value("\${schema-registry-url}") schemaRegistryUrl: String
) {

    private val consumer: KafkaConsumer<String, GenericRecord> by lazy {
        val consumerProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "http://localhost:9092",
            AUTO_OFFSET_RESET_CONFIG to "earliest",
            KEY_DESERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.java,
            GROUP_ID_CONFIG to "tms-dashboard-api",
            SECURITY_PROTOCOL_CONFIG to "PLAINTEXT",
            "schema.registry.url" to schemaRegistryUrl
        )

        KafkaConsumer<String, GenericRecord>(consumerProps)
            .also { it.subscribe(listOf(TRANSACTION_CREATED_TOPIC)) }
    }

    @Scheduled(fixedDelay = 1000)
    fun consume() {
        while (true) {
            val events = consumer
                .poll(Duration.ofSeconds(1))
                .map { Avro.default.fromRecord(TransactionCreatedAvroEvent.serializer(), it.value()) }

            for (event in events) {
                println("Received Avro event: $event")
            }
        }
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.avro.transaction.created"
    }
}