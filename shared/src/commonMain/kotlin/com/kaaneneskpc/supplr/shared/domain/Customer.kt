package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class Customer(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val phoneNumber: PhoneNumber? = null,
    val cart: List<CartItem> = emptyList(),
    val isAdmin: Boolean = false,
    val profilePhotoUrl: String? = null,
    val birthDate: Long? = null,
    val communicationPreferences: CommunicationPreferences? = null,
    val isTwoFactorEnabled: Boolean = false,
    val rewardPoints: Int = 0
)

@Serializable
data class PhoneNumber(
    val dialCode: Int,
    val number: String
)


