package com.stosik.kafka.producer.protobuf

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class TransactionCreatedProtobufEvent(
    val id: UUID,
    val hostPaymentId: UUID,
    val platformPaymentId: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
)