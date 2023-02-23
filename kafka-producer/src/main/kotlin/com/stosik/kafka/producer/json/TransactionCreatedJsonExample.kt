package com.stosik.kafka.producer.json

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.util.*

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