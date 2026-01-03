package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class CartItem(
    val id: String = Uuid.random().toHexString(),
    val productId: String,
    val flavor: String? = null,
    val quantity: Int,
    val title: String = "",
    val thumbnail: String = "",
    val price: Double = 0.0
)