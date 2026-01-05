package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class Review(
    val id: String,
    val productId: String,
    val userId: String,
    val username: String,
    val rating: Float,
    val comment: String,
    val photoUrls: List<String> = emptyList(),
    val helpfulCount: Int = 0,
    val unhelpfulCount: Int = 0,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isVerifiedPurchase: Boolean = false,
    val isApproved: Boolean = true
) 