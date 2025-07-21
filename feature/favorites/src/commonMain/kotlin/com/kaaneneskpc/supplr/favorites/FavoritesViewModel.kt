package com.kaaneneskpc.supplr.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.FavoritesRepository
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    val favoriteProducts: StateFlow<RequestState<List<Product>>> =
        favoritesRepository.readFavoriteProducts().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    fun addToFavorites(productId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            favoritesRepository.addProductToFavorites(productId).collect { state ->
                when (state) {
                    is RequestState.Success -> onSuccess()
                    is RequestState.Error -> onError(state.message)
                    else -> {}
                }
            }
        }
    }

    fun removeFromFavorites(productId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            favoritesRepository.removeProductFromFavorites(productId).collect { state ->
                when (state) {
                    is RequestState.Success -> onSuccess()
                    is RequestState.Error -> onError(state.message)
                    else -> {}
                }
            }
        }
    }
} 