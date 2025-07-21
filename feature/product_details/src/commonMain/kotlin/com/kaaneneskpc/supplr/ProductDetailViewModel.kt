package com.kaaneneskpc.supplr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.data.domain.ProductRepository
import com.kaaneneskpc.supplr.data.domain.FavoritesRepository
import com.kaaneneskpc.supplr.shared.domain.CartItem
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel (
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    private val favoritesRepository: FavoritesRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val product = productRepository.readProductByIdFlow(
        savedStateHandle.get<String>("id") ?: ""
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

    val favoriteProductIds: StateFlow<RequestState<List<String>>> =
        favoritesRepository.readFavoriteProductIds()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RequestState.Loading
            )

    var quantity by mutableStateOf(1)
        private set

    var selectedFlavor: String? by mutableStateOf(null)
        private set

    fun updateQuantity(value: Int) {
        quantity = value
    }

    fun updateFlavor(value: String) {
        selectedFlavor = value
    }

    fun addItemToCart(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val productId = savedStateHandle.get<String>("id")
            if (productId != null) {
                customerRepository.addItemToCard(
                    cartItem = CartItem(
                        productId = productId,
                        flavor = selectedFlavor,
                        quantity = quantity
                    ),
                    onSuccess = onSuccess,
                    onError = onError
                )
            } else {
                onError("Product id is not found.")
            }
        }
    }

    fun addToFavorites(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val productId = savedStateHandle.get<String>("id")
            if (productId != null) {
                favoritesRepository.addProductToFavorites(productId).collect { state ->
                    when (state) {
                        is RequestState.Success -> onSuccess()
                        is RequestState.Error -> onError(state.message)
                        else -> {}
                    }
                }
            } else {
                onError("Product id is not found.")
            }
        }
    }
}