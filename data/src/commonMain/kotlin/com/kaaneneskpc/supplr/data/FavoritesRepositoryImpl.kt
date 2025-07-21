package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.FavoritesRepository
import com.kaaneneskpc.supplr.data.domain.ProductRepository
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

class FavoritesRepositoryImpl(
    private val productRepository: ProductRepository
) : FavoritesRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override fun addProductToFavorites(productId: String): Flow<RequestState<Unit>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val db = Firebase.firestore
                db.collection("favorites")
                    .document(userId)
                    .collection("items")
                    .document(productId)
                    .set(mapOf("productId" to productId))
                send(RequestState.Success(Unit))
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while adding favorite: ${e.message}"))
        }
    }

    override fun removeProductFromFavorites(productId: String): Flow<RequestState<Unit>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val db = Firebase.firestore
                db.collection("favorites")
                    .document(userId)
                    .collection("items")
                    .document(productId)
                    .delete()
                send(RequestState.Success(Unit))
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while removing favorite: ${e.message}"))
        }
    }

    override fun readFavoriteProductIds(): Flow<RequestState<List<String>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val db = Firebase.firestore
                db.collection("favorites")
                    .document(userId)
                    .collection("items")
                    .snapshots
                    .collectLatest { query ->
                        val ids = query.documents.mapNotNull { it.get<String>("productId") }
                        send(RequestState.Success(ids))
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading favorites: ${e.message}"))
        }
    }

    override fun readFavoriteProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            readFavoriteProductIds().collectLatest { state ->
                if (state is RequestState.Success) {
                    val ids = state.data
                    if (ids.isNotEmpty()) {
                        productRepository.readProductsByIdsFlow(ids).collectLatest { productsState ->
                            send(productsState)
                        }
                    } else {
                        send(RequestState.Success(emptyList()))
                    }
                } else if (state is RequestState.Error) {
                    send(RequestState.Error(state.message))
                }
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading favorite products: ${e.message}"))
        }
    }
} 