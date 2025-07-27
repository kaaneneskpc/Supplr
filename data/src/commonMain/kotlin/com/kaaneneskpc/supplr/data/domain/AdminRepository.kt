package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Customer
import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getCurrentUserId(): String?
    suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun uploadImageToStorage(file: File): String?
    suspend fun deleteImageFromStorage(
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    fun readLastTenProducts(): Flow<RequestState<List<Product>>>
    suspend fun readProductById(id: String): RequestState<Product>
    suspend fun updateProductThumbnail(
        productId: String,
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    fun searchProductsByTitle(
        searchQuery: String,
    ): Flow<RequestState<List<Product>>>
    
    // Analytics Functions
    suspend fun getOrdersByDateRange(
        startDate: Long,
        endDate: Long
    ): RequestState<List<Order>>
    
    suspend fun getAllUsers(): RequestState<List<Customer>>
    
    suspend fun getTotalOrdersCount(): RequestState<Int>
    
    suspend fun getTotalUsersCount(): RequestState<Int>
}