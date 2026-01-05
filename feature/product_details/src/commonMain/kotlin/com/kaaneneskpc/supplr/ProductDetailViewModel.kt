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
import com.kaaneneskpc.supplr.data.domain.ReviewRepository
import com.kaaneneskpc.supplr.shared.domain.CartItem
import com.kaaneneskpc.supplr.shared.domain.Review
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductDetailViewModel (
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    private val favoritesRepository: FavoritesRepository,
    private val reviewRepository: ReviewRepository,
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

    val reviews: StateFlow<RequestState<List<Review>>> =
        reviewRepository.getReviewsForProduct(
            savedStateHandle.get<String>("id") ?: ""
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    private val _userVotes = MutableStateFlow<Map<String, Boolean?>>(emptyMap())
    val userVotes: StateFlow<Map<String, Boolean?>> = _userVotes.asStateFlow()

    val averageRating: StateFlow<Float> = reviews.map { state ->
        when (state) {
            is RequestState.Success -> {
                if (state.data.isNotEmpty()) {
                    state.data.map { it.rating }.average().toFloat()
                } else {
                    0f
                }
            }
            else -> 0f
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    val reviewCount: StateFlow<Int> = reviews.map { state ->
        when (state) {
            is RequestState.Success -> state.data.size
            else -> 0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    var quantity by mutableStateOf(1)
        private set

    var selectedFlavor: String? by mutableStateOf(null)
        private set

    var hasUserReviewed by mutableStateOf(false)
        private set

    var isUploadingPhotos by mutableStateOf(false)
        private set

    var uploadedPhotoUrls by mutableStateOf<List<String>>(emptyList())
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
            val currentProduct = product.value
            if (productId != null && currentProduct.isSuccess()) {
                val productData = currentProduct.getSuccessData()
                customerRepository.addItemToCard(
                    cartItem = CartItem(
                        productId = productId,
                        flavor = selectedFlavor,
                        quantity = quantity,
                        title = productData.title,
                        thumbnail = productData.thumbnail,
                        price = productData.price
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

    fun loadReviewStats() {
        viewModelScope.launch {
            val productId = savedStateHandle.get<String>("id") ?: return@launch
            when (val result = reviewRepository.hasUserReviewedProduct(productId)) {
                is RequestState.Success -> hasUserReviewed = result.data
                else -> hasUserReviewed = false
            }
        }
    }

    fun uploadPhotos(
        files: List<File>,
        onComplete: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isUploadingPhotos = true
            val urls = mutableListOf<String>()
            val productId = savedStateHandle.get<String>("id") ?: run {
                isUploadingPhotos = false
                onError("Product id is not found.")
                return@launch
            }
            for (file in files) {
                reviewRepository.uploadReviewPhoto(
                    file = file,
                    reviewId = productId,
                    onSuccess = { url -> urls.add(url) },
                    onError = { error ->
                        isUploadingPhotos = false
                        onError(error)
                        return@uploadReviewPhoto
                    }
                )
            }
            uploadedPhotoUrls = urls
            isUploadingPhotos = false
            onComplete(urls)
        }
    }

    fun addReview(
        rating: Float,
        comment: String,
        photoUrls: List<String> = emptyList(),
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val productId = savedStateHandle.get<String>("id")
            if (productId != null) {
                reviewRepository.addReview(
                    productId = productId,
                    rating = rating,
                    comment = comment,
                    photoUrls = photoUrls,
                    onSuccess = {
                        uploadedPhotoUrls = emptyList()
                        onSuccess()
                        loadReviewStats()
                    },
                    onError = onError
                )
            } else {
                onError("Product id is not found.")
            }
        }
    }

    fun voteReview(
        reviewId: String,
        isHelpful: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            reviewRepository.voteReview(
                reviewId = reviewId,
                isHelpful = isHelpful,
                onSuccess = {
                    val currentVotes = _userVotes.value.toMutableMap()
                    currentVotes[reviewId] = isHelpful
                    _userVotes.value = currentVotes
                    onSuccess()
                },
                onError = onError
            )
        }
    }

    fun loadUserVoteForReview(reviewId: String) {
        viewModelScope.launch {
            when (val result = reviewRepository.getUserVoteForReview(reviewId)) {
                is RequestState.Success -> {
                    val currentVotes = _userVotes.value.toMutableMap()
                    currentVotes[reviewId] = result.data
                    _userVotes.value = currentVotes
                }
                else -> {}
            }
        }
    }

    fun removeVote(
        reviewId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            reviewRepository.removeVote(
                reviewId = reviewId,
                onSuccess = {
                    val currentVotes = _userVotes.value.toMutableMap()
                    currentVotes[reviewId] = null
                    _userVotes.value = currentVotes
                    onSuccess()
                },
                onError = onError
            )
        }
    }

    fun clearUploadedPhotos() {
        uploadedPhotoUrls = emptyList()
    }

    init {
        loadReviewStats()
    }
}