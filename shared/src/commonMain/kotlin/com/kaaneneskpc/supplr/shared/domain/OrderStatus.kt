package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus(val displayName: String, val icon: String) {
    PENDING("Pending", "⏳"),
    CONFIRMED("Confirmed", "✅"),
    PREPARING("Preparing", "📦"),
    SHIPPED("Shipped", "🚚"),
    DELIVERED("Delivered", "🎉"),
    CANCELLED("Cancelled", "❌");

    companion object {
        fun fromString(value: String): OrderStatus {
            return entries.find { it.name == value } ?: PENDING
        }
    }
}
