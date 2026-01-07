package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable

@Serializable
enum class CouponType {
    PERCENTAGE,
    FIXED_AMOUNT,
    FREE_SHIPPING
}

@Serializable
data class Coupon(
    val id: String = "",
    val code: String = "",
    val type: CouponType = CouponType.PERCENTAGE,
    val value: Double = 0.0,
    val minimumOrderAmount: Double = 0.0,
    val maximumDiscount: Double? = null,
    val usageLimit: Int? = null,
    val usageCount: Int = 0,
    val expirationDate: Long = 0L,
    val isActive: Boolean = true,
    val createdAt: Long = 0L
) {
    @OptIn(kotlin.time.ExperimentalTime::class)
    fun isExpired(): Boolean {
        return kotlin.time.Clock.System.now().toEpochMilliseconds() > expirationDate
    }
    fun hasReachedUsageLimit(): Boolean {
        return usageLimit != null && usageCount >= usageLimit
    }
    fun meetsMinimumOrderAmount(orderAmount: Double): Boolean {
        return orderAmount >= minimumOrderAmount
    }
    fun calculateDiscount(orderAmount: Double): Double {
        return when (type) {
            CouponType.PERCENTAGE -> {
                val discount = orderAmount * (value / 100.0)
                maximumDiscount?.let { minOf(discount, it) } ?: discount
            }
            CouponType.FIXED_AMOUNT -> {
                minOf(value, orderAmount)
            }
            CouponType.FREE_SHIPPING -> 0.0
        }
    }
}

sealed class CouponValidationResult {
    data class Valid(
        val coupon: Coupon,
        val discountAmount: Double
    ) : CouponValidationResult()
    data class Invalid(
        val message: String
    ) : CouponValidationResult()
}
