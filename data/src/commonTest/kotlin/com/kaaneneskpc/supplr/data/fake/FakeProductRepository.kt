package com.kaaneneskpc.supplr.data.fake

import com.kaaneneskpc.supplr.data.domain.ProductRepository
import com.kaaneneskpc.supplr.shared.domain.PaginatedResult
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.domain.ProductCategory
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()
    private var currentUserId: String? = "test-user-id"
    var shouldReturnError = false
    var errorMessage = "Test error"
    fun setProducts(productList: List<Product>) {
        products.clear()
        products.addAll(productList)
    }
    fun addProduct(product: Product) {
        products.add(product)
    }
    fun clearProducts() {
        products.clear()
    }
    fun setCurrentUser(userId: String?) {
        currentUserId = userId
    }
    override fun getCurrentUserId(): String? = currentUserId
    override fun readDiscountedProducts(): Flow<RequestState<List<Product>>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        val discountedProducts = products.filter { it.isDiscounted }
        emit(RequestState.Success(discountedProducts))
    }
    override fun readNewProducts(): Flow<RequestState<List<Product>>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        val newProducts = products.filter { it.isNew }
        emit(RequestState.Success(newProducts))
    }
    override fun readProductByIdFlow(id: String): Flow<RequestState<Product>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        val product = products.find { it.id == id }
        if (product != null) {
            emit(RequestState.Success(product))
        } else {
            emit(RequestState.Error("Product not found"))
        }
    }
    override fun readProductsByIdsFlow(ids: List<String>): Flow<RequestState<List<Product>>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        val foundProducts = products.filter { it.id in ids }
        emit(RequestState.Success(foundProducts))
    }
    override fun readProductsByCategoryFlow(category: ProductCategory): Flow<RequestState<List<Product>>> = flow {
        emit(RequestState.Loading)
        if (shouldReturnError) {
            emit(RequestState.Error(errorMessage))
            return@flow
        }
        val categoryProducts = products.filter { it.category == category.title }
        emit(RequestState.Success(categoryProducts))
    }
    override suspend fun readProductsByCategoryPaginated(
        category: ProductCategory,
        pageSize: Int,
        lastDocumentId: String?
    ): RequestState<PaginatedResult<Product>> {
        if (shouldReturnError) {
            return RequestState.Error(errorMessage)
        }
        val categoryProducts = products.filter { it.category == category.title }
        val startIndex = if (lastDocumentId == null) {
            0
        } else {
            val lastIndex = categoryProducts.indexOfFirst { it.id == lastDocumentId }
            if (lastIndex == -1) 0 else lastIndex + 1
        }
        val endIndex = minOf(startIndex + pageSize, categoryProducts.size)
        val pageItems = categoryProducts.subList(startIndex, endIndex)
        val hasNextPage = endIndex < categoryProducts.size
        val lastId = if (pageItems.isNotEmpty()) pageItems.last().id else null
        return RequestState.Success(
            PaginatedResult(
                items = pageItems,
                lastDocumentId = lastId,
                hasNextPage = hasNextPage,
                totalCount = categoryProducts.size
            )
        )
    }
}
