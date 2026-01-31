package com.kaaneneskpc.supplr.categories.category_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.ProductRepository
import com.kaaneneskpc.supplr.shared.domain.PaginatedResult
import com.kaaneneskpc.supplr.shared.domain.PaginationState
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.domain.ProductCategory
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class CategorySearchViewModel(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val category: ProductCategory = ProductCategory.valueOf(
        savedStateHandle.get<String>("category") ?: ProductCategory.Meat.name
    )
    private val pageSize: Int = 10
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products
    private val _paginationState = MutableStateFlow<PaginationState>(PaginationState.Idle)
    val paginationState: StateFlow<PaginationState> = _paginationState
    private var lastDocumentId: String? = null
    private var hasNextPage: Boolean = true
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadInitialProducts()
    }

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
    }

    val filteredProducts: StateFlow<RequestState<List<Product>>> = combine(
        _products,
        searchQuery.debounce(500),
        _paginationState
    ) { productList, query, pagState ->
        when {
            pagState is PaginationState.Loading && productList.isEmpty() -> RequestState.Loading
            pagState is PaginationState.Error && productList.isEmpty() -> RequestState.Error(pagState.message)
            query.isBlank() -> RequestState.Success(productList)
            else -> RequestState.Success(
                productList.filter { it.title.lowercase().contains(query.lowercase()) }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

    private fun loadInitialProducts() {
        viewModelScope.launch {
            _paginationState.value = PaginationState.Loading
            lastDocumentId = null
            hasNextPage = true
            _products.value = emptyList()
            loadProductsPage()
        }
    }

    fun loadNextPage() {
        if (_paginationState.value is PaginationState.LoadingMore) return
        if (!hasNextPage) return
        if (_paginationState.value is PaginationState.EndReached) return
        viewModelScope.launch {
            _paginationState.value = PaginationState.LoadingMore
            loadProductsPage()
        }
    }

    private suspend fun loadProductsPage() {
        val result = productRepository.readProductsByCategoryPaginated(
            category = category,
            pageSize = pageSize,
            lastDocumentId = lastDocumentId
        )
        when (result) {
            is RequestState.Success -> {
                val paginatedResult: PaginatedResult<Product> = result.getSuccessData()
                val currentProducts = _products.value.toMutableList()
                currentProducts.addAll(paginatedResult.items)
                _products.value = currentProducts.distinctBy { it.id }
                lastDocumentId = paginatedResult.lastDocumentId
                hasNextPage = paginatedResult.hasNextPage
                _paginationState.value = if (hasNextPage) {
                    PaginationState.Idle
                } else {
                    PaginationState.EndReached
                }
            }
            is RequestState.Error -> {
                _paginationState.value = PaginationState.Error(result.getErrorMessage())
            }
            else -> {}
        }
    }

    fun refreshProducts() {
        loadInitialProducts()
    }
}