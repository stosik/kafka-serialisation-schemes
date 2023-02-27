package com.stosik.kafka.consumer.protobuf

import com.stosik.kafka.models.protobuf.TransactionCreatedProtobufEvent
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE
import org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pl.stosik.TransactionCreatedEventOuterClass.TransactionCreatedEvent
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.*



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
        SPECIFIC_PROTOBUF_VALUE_TYPE to TransactionCreatedEvent::class.java,
        "schema.registry.url" to schemaRegistryUrl
    )

    private val consumer = KafkaConsumer<String, TransactionCreatedEvent>(consumerProps).also {
        it.subscribe(listOf(TRANSACTION_CREATED_TOPIC))
    }

    @Scheduled(fixedRate = 2000)
    fun consume() {
        val events = consumer
            .poll(Duration.ofSeconds(1))
            .map { it.value().toProtoEvent() }

        for (event in events) {
            println("Received Proto event: $event")
        }
    }

    private fun TransactionCreatedEvent.toProtoEvent() = TransactionCreatedProtobufEvent(
        id = UUID.fromString(this.id),
        hostPaymentId = UUID.fromString(this.hostPaymentId),
        platformPaymentId = this.platformPaymentId,
        createdAt = LocalDateTime.parse(this.createdAt),
        amount = BigDecimal(
            BigInteger(this.amount.value.toByteArray()),
            this.amount.scale,
            MathContext(this.amount.precision)
        )
    )

    companion object {

        private const val TRANSACTION_CREATED_TOPIC = "pl.stosik.proto.transaction.created"
    }
}