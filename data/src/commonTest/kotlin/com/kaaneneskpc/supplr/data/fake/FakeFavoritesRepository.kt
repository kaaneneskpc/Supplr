package com.kaaneneskpc.supplr.data.fake

import com.kaaneneskpc.supplr.data.domain.FavoritesRepository
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFavoritesRepository : FavoritesRepository {
    private val favoriteProductIds = mutableListOf<String>()
    private val products = mutableMapOf<String, Product>()
    private var currentUserId: String? = "test-user-id"
    var shouldReturnError = false
    var errorMessage = "Test error"
    fun setCurrentUser(userId: String?) {
        currentUserId = userId
    }
    fun addProductToStore(product: Product) {
        products[product.id] = product
    }
    fun clearFavorites() {
        favoriteProductIds.clear()
    }
    override fun getCurrentUserId(): String? = currentUserId
    override fun addProductToFavorites(productId: String): Flow<RequestState<Unit>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        if (currentUserId == null) {
            emit(RequestState.Error("User not authenticated"))
            return@flow
        }
        if (!favoriteProductIds.contains(productId)) {
            favoriteProductIds.add(productId)
        }
        emit(RequestState.Success(Unit))
    }
    override fun removeProductFromFavorites(productId: String): Flow<RequestState<Unit>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        if (currentUserId == null) {
            emit(RequestState.Error("User not authenticated"))
            return@flow
        }
        favoriteProductIds.remove(productId)
        emit(RequestState.Success(Unit))
    }
    override fun readFavoriteProductIds(): Flow<RequestState<List<String>>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        emit(RequestState.Success(favoriteProductIds.toList()))
    }
    override fun readFavoriteProducts(): Flow<RequestState<List<Product>>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        val favoriteProducts = favoriteProductIds.mapNotNull { id -> products[id] }
        emit(RequestState.Success(favoriteProducts))
    }
}
