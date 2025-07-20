package com.kaaneneskpc.supplr.shared.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class Screen {
    @Serializable
    data object Auth : Screen()
    @Serializable
    data object Home : Screen()
    @Serializable
    data object Products : Screen()
    @Serializable
    data object Cart : Screen()
    @Serializable
    data object Categories : Screen()
    @Serializable
    data object Profile : Screen()
    @Serializable
    data object AdminPanel : Screen()
    @Serializable
    data class ManageProduct(val id: String? = null) : Screen()
    @Serializable
    data class ProductDetails(val id: String) : Screen()
    @Serializable
    data class CategorySearch(
        val category: String
    ) : Screen()

    @Serializable
    data class Checkout(
        val totalAmount: String
    ) : Screen()

    @Serializable
    data class PaymentCompleted(
        val isSuccess: Boolean? = null,
        val error: String? = null,
        val token: String? = null
    ) : Screen()
    @Serializable
    data object ContactUs : Screen()
}