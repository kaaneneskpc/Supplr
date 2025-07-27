package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Serializable
data class Order(
    val orderId: String = Uuid.random().toHexString(),
    val customerId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val token: String? = null,
    val currency: String = "usd",
    val paymentIntentId: String? = null,
    val status: String = "PENDING",
    val shippingAddress: String = ""
)