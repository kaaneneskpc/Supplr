package com.kaaneneskpc.supplr.shared.domain.gamification

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(
    val userId: String,
    val userName: String,
    val profilePhotoUrl: String? = null,
    val totalSpent: Double,
    val totalOrders: Int,
    val rank: Int
)
