package com.stosik.kafka.producer.json

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class TransactionCreatedJsonEvent(
    val id: UUID,
    val hostPaymentId: UUID,
    val platformPaymentId: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
)