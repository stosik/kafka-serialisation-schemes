package com.stosik.kafka.consumer.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.stosik.kafka.models.consumer.kafkaConsumer
import com.stosik.kafka.models.json.TransactionCreatedJsonEvent
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

@Component
internal class TransactionKafkaJsonConsumer(private val objectMapper: ObjectMapper) {

    private val consumer = kafkaConsumer<String, String> {
        configuration {
            bootstrapServers = listOf("http://localhost:9092")
            autoOffsetReset = "earliest"
            keyDeserializer = StringDeserializer::class.java
            valueDeserializer = StringDeserializer::class.java
            groupId = "tms-dashboard-api-json"
            securityProtocol = "PLAINTEXT"
        }
        topics {
            topic(TRANSACTION_CREATED_TOPIC)
        }
    }

    @Scheduled(fixedRate = 2000)
    fun consume() {
        val events = consumer
            .poll(Duration.ofSeconds(1))
            .map { objectMapper.readValue(it.value(), TransactionCreatedJsonEvent::class.java) }

        for (event in events) {
            println("Received JSON event: $event")
        }
    }

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.json.transaction.created"
    }
}


