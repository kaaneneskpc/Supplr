package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class LocationCategory(
    val displayName: String,
    val icon: String
) {
    HOME("Home", "🏠"),
    WORK("Work", "🏢"),
    FAMILY("Family", "👨‍👩‍👧‍👦"),
    FRIENDS("Friends", "👥"),
    SHOPPING("Shopping", "🛍️"),
    OTHER("Other", "📍")
}

@OptIn(ExperimentalTime::class)
@Serializable
data class Location(
    val id: String,
    val userId: String,
    val title: String, // e.g., "Home", "Office", "Work"
    val category: LocationCategory = LocationCategory.OTHER,
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