package com.stosik.kafka.producer.avro

import com.sksamuel.avro4k.AvroName
import com.sksamuel.avro4k.AvroNamespace
import com.sksamuel.avro4k.serializer.BigDecimalSerializer
import com.sksamuel.avro4k.serializer.LocalDateTimeSerializer
import com.sksamuel.avro4k.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Serializable
@AvroName("TransactionCreatedEvent")
@AvroNamespace("com.stosik.transaction")
data class TransactionCreatedAvroEvent(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @Serializable(with = UUIDSerializer::class) val hostPaymentId: UUID,
    val platformPaymentId: String,
    @Serializable(with = BigDecimalSerializer::class) val amount: BigDecimal,
    @Serializable(with = LocalDateTimeSerializer::class) val createdAt: LocalDateTime,
)