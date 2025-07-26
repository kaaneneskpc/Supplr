package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class Order(
    val orderId: String = Uuid.random().toHexString(),
    val customerId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val token: String? = null,
    // Stripe payment fields
    val currency: String = "usd",
    val paymentIntentId: String? = null,
    val status: String = "PENDING",
    val shippingAddress: String = ""
)