package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class Location(
    val id: String,
    val userId: String,
    val title: String, // e.g., "Home", "Office", "Work"
    val fullAddress: String, // Complete street address
    val city: String,
    val state: String? = null, // State/Province (optional)
    val postalCode: String,
    val country: String,
    val isDefault: Boolean = false, // Is this the default delivery address
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val latitude: Double? = null, // For future map integration
    val longitude: Double? = null // For future map integration
) 