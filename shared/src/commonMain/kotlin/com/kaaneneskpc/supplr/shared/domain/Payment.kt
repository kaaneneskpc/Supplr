package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class PaymentIntent(
    val id: String,
    val clientSecret: String,
    val amount: Long,
    val currency: String,
    val status: PaymentStatus
)

@Serializable
data class PaymentIntentRequest(
    val amount: Long,
    val currency: String = "usd",
    val automaticPaymentMethods: Map<String, Boolean> = mapOf("enabled" to true)
)

@Serializable
data class PaymentIntentResponse(
    val id: String,
    val client_secret: String,
    val amount: Long,
    val currency: String,
    val status: String
)

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    CANCELED
}

data class PaymentResult(
    val isSuccess: Boolean,
    val paymentIntentId: String?,
    val errorMessage: String?
)

 