package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getCurrentUserId(): String?
    fun addProductToFavorites(productId: String): Flow<RequestState<Unit>>
    fun removeProductFromFavorites(productId: String): Flow<RequestState<Unit>>
    fun readFavoriteProductIds(): Flow<RequestState<List<String>>>
    fun readFavoriteProducts(): Flow<RequestState<List<Product>>>
} 