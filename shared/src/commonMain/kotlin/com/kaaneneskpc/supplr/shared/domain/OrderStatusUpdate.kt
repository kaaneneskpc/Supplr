package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class OrderStatusUpdate(
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val note: String? = null
)
