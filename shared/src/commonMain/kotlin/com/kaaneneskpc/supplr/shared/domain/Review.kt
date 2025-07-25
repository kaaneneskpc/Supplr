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
    val username: String, // Display name for the reviewer
    val rating: Float, // Rating out of 5
    val comment: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isVerifiedPurchase: Boolean = false, // Whether the user actually bought the product
    val isApproved: Boolean = true // Admin approval status (default to true for auto-approval)
) 