package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class ReviewVote(
    val id: String,
    val reviewId: String,
    val userId: String,
    val isHelpful: Boolean,
    val votedAt: Long = Clock.System.now().toEpochMilliseconds()
)
