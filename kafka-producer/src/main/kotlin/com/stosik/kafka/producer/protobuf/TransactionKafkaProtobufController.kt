package com.stosik.kafka.producer.protobuf

import com.stosik.kafka.producer.asyncSend
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TransactionKafkaProtobufController(
    @Value("\${schema-registry-url}") schemaRegistryUrl: String
) {

    private val kafkaProducer: KafkaProducer<String, TransactionCreatedProtobufEvent> by lazy {
        val producerProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "http://localhost:9092",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java,
            SECURITY_PROTOCOL_CONFIG to "PLAINTEXT",
            "schema.registry.url" to schemaRegistryUrl
        )

        KafkaProducer<String, TransactionCreatedProtobufEvent>(producerProps)
    }


    @GetMapping("/proto/transactions/created")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTransactionEvent() {
        val event = TransactionCreatedProtobufEventExample.random()
        kafkaProducer.asyncSend(ProducerRecord(TRANSACTION_CREATED_TOPIC, event))
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.proto.transaction.created"
    }
}