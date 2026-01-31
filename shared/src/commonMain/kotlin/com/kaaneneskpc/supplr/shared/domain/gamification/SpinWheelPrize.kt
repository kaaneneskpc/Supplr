package com.kaaneneskpc.supplr.shared.domain.gamification

import kotlinx.serialization.Serializable

@Serializable
data class SpinWheelPrize(
    val id: String,
    val name: String,
    val prizeType: PrizeType,
    val value: Double,
    val couponCode: String? = null,
    val color: Long
)

@Serializable
enum class PrizeType {
    DISCOUNT_PERCENTAGE,
    DISCOUNT_FIXED,
    FREE_SHIPPING,
    POINTS,
    EMPTY;

    companion object {
        fun fromString(value: String): PrizeType {
            return entries.find { it.name == value } ?: EMPTY
        }
    }
}
