package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class CommunicationPreferences(
    val isEmailEnabled: Boolean = true,
    val isPushEnabled: Boolean = true,
    val isSmsEnabled: Boolean = false
)
