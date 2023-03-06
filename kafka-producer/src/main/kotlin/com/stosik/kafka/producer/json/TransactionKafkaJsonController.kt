package com.stosik.kafka.producer.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.stosik.kafka.models.json.TransactionCreatedJsonEvent
import com.stosik.kafka.models.producer.asyncSend
import com.stosik.kafka.models.producer.kafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@RestController
internal class TransactionKafkaJsonController(private val objectMapper: ObjectMapper) {

    private val kafkaProducer = kafkaProducer<String, String> {
        configuration {
            bootstrapServers = listOf("http://localhost:9092")
            keyDeserializer = StringSerializer::class.java
            valueDeserializer = StringSerializer::class.java
            securityProtocol = "PLAINTEXT"
        }
    }

    @GetMapping("/json/transactions/created")
    @ResponseStatus(CREATED)
    suspend fun createTransactionEvent() {
        val event = TransactionCreatedJsonEventExample.random()
        val jsonEvent = event.toJson()
        sendEvent(event.id, jsonEvent)
    }

    @GetMapping("/json/transactions/spam")
    @ResponseStatus(CREATED)
    suspend fun spamEvents() {
        (0..50)
            .map { TransactionCreatedJsonEventExample.random() }
            .associate { it.id to it.toJson() }
            .forEach { sendEvent(it.key, it.value) }
    }

    private fun TransactionCreatedJsonEvent.toJson() = objectMapper.writeValueAsString(this)

    private suspend fun sendEvent(id: UUID, event: String) {
        kafkaProducer.asyncSend(ProducerRecord(TRANSACTION_CREATED_TOPIC, id.toString(), event))
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.json.transaction.created"
    }
}

object TransactionCreatedJsonEventExample {

    fun random(): TransactionCreatedJsonEvent {
        return TransactionCreatedJsonEvent(
            id = UUID.randomUUID(),
            hostPaymentId = UUID.randomUUID(),
            createdAt = LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0),
            platformPaymentId = "4a8ee61cdf3e4842b33c56b55df4cc251d233422",
            amount = BigDecimal.valueOf(12.50),
        )
    }
}