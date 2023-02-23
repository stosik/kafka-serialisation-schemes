package com.stosik.kafka.producer.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.stosik.kafka.producer.asyncSend
import org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
internal class TransactionKafkaJsonController(
    private val objectMapper: ObjectMapper,
) {

    private val kafkaProducer: KafkaProducer<String, String> by lazy {
        val producerProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "http://localhost:9092",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            "value.serializer.json.mapper" to objectMapper,
            SECURITY_PROTOCOL_CONFIG to "PLAINTEXT"
        )

        KafkaProducer<String, String>(producerProps)
    }


    @GetMapping("/json/transactions/created")
    @ResponseStatus(CREATED)
    suspend fun createTransactionEvent() {
        val event = TransactionCreatedJsonEventExample.random()
        val jsonEvent = objectMapper.writeValueAsString(event)
        kafkaProducer.asyncSend(ProducerRecord(TRANSACTION_CREATED_TOPIC, jsonEvent))
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.json.transaction.created"
    }
}


