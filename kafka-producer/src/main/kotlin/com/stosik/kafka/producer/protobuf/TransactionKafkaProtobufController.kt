package com.stosik.kafka.producer.protobuf

import com.stosik.kafka.models.producer.asyncSend
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
import pl.stosik.TransactionCreatedEventOuterClass.TransactionCreatedEvent
import pl.stosik.transactionCreatedEvent
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@RestController
internal class TransactionKafkaProtobufController(
    @Value("\${schema-registry-url}") schemaRegistryUrl: String
) {

    private val kafkaProducer: KafkaProducer<String, TransactionCreatedEvent> by lazy {
        val producerProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "http://localhost:9092",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java,
            SECURITY_PROTOCOL_CONFIG to "PLAINTEXT",
            "schema.registry.url" to schemaRegistryUrl
        )

        KafkaProducer<String, TransactionCreatedEvent>(producerProps)
    }

    @GetMapping("/proto/transactions/created")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTransactionEvent() {
        val event = TransactionCreatedProtobufEventExample.random()
        sendEvent(event)
    }

    @GetMapping("/proto/transactions/spam")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun spamEvents() {
        (0..50)
            .map { TransactionCreatedProtobufEventExample.random() }
            .forEach { sendEvent(it) }
    }

    private suspend fun sendEvent(event: TransactionCreatedEvent) {
        kafkaProducer.asyncSend(ProducerRecord(TRANSACTION_CREATED_TOPIC, event))
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.proto.transaction.created"
    }
}

object TransactionCreatedProtobufEventExample {

    fun random() = transactionCreatedEvent {
        val decimalAmount = BigDecimal.valueOf(12.50)
        id = UUID.randomUUID().toString()
        hostPaymentId = UUID.randomUUID().toString()
        platformPaymentId = "4a8ee61cdf3e4842b33c56b55df4cc251d233422"
        createdAt = LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0).toString()
        amount = decimalAmount.toString()
    }
}