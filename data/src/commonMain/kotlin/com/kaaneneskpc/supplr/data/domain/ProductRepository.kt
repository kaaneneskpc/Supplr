package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getCurrentUserId(): String?
    fun readDiscountedProducts(): Flow<RequestState<List<Product>>>
    fun readNewProducts(): Flow<RequestState<List<Product>>>
}