package com.stosik.kafka.producer.avro

import com.sksamuel.avro4k.Avro
import com.stosik.kafka.models.avro.TransactionCreatedAvroEvent
import com.stosik.kafka.producer.asyncSend
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericRecord
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
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@RestController
internal class TransactionKafkaAvroController(
    @Value("\${schema-registry-url}") schemaRegistryUrl: String
) {

    private val kafkaProducer: KafkaProducer<String, GenericRecord> by lazy {
        val producerProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "http://localhost:9092",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to KafkaAvroSerializer::class.java,
            SECURITY_PROTOCOL_CONFIG to "PLAINTEXT",
            "schema.registry.url" to schemaRegistryUrl,
//            "auto.register.schemas property" to false
        )

        KafkaProducer<String, GenericRecord>(producerProps)
    }


    @GetMapping("/avro/transactions/created")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTransactionEvent() {
        val event = TransactionCreatedAvroEventExample.random()
        val avroRecord = event.toAvroRecord()
        sendEvent(event.id, avroRecord)
    }

    @GetMapping("/avro/transactions/spam")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun spamEvents() {
        (0..50)
            .map { TransactionCreatedAvroEventExample.random() }
            .associate { it.id to it.toAvroRecord() }
            .forEach { sendEvent(it.key, it.value) }
    }

    private fun TransactionCreatedAvroEvent.toAvroRecord() =
        Avro.default.toRecord(TransactionCreatedAvroEvent.serializer(), this)


    private suspend fun sendEvent(id: UUID, avroRecord: GenericRecord) {
        kafkaProducer.asyncSend(ProducerRecord(TRANSACTION_CREATED_TOPIC, id.toString(), avroRecord))
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.avro.transaction.created"
    }
}

object TransactionCreatedAvroEventExample {

    fun random(): TransactionCreatedAvroEvent {
        return TransactionCreatedAvroEvent(
            id = UUID.randomUUID(),
            hostPaymentId = UUID.randomUUID(),
            createdAt = LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0),
            platformPaymentId = "4a8ee61cdf3e4842b33c56b55df4cc251d233422",
            amount = BigDecimal.valueOf(12.50),
        )
    }
}