package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.domain.OrderStatus
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getCurrentUserId(): String?
    suspend fun createTheOrder(
        order: Order,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    fun getOrdersByCustomerId(): Flow<RequestState<List<Order>>>
    fun getOrderById(orderId: String): Flow<RequestState<Order?>>
    fun getAllOrders(): Flow<RequestState<List<Order>>>
    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        note: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}