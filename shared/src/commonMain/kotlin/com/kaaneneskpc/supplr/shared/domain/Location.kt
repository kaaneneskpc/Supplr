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
    val title: String,
    val category: LocationCategory = LocationCategory.OTHER,
    val fullAddress: String,
    val city: String,
    val state: String? = null,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean = false,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val latitude: Double? = null,
    val longitude: Double? = null
) 