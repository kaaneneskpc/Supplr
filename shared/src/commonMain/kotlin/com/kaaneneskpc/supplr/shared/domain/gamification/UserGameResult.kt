package com.kaaneneskpc.supplr.shared.domain.gamification

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Serializable
data class UserGameResult(
    val id: String = Uuid.random().toHexString(),
    val userId: String,
    val prizeId: String,
    val prizeName: String,
    val prizeValue: Double,
    val prizeType: PrizeType,
    val couponCode: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isRedeemed: Boolean = false
)
