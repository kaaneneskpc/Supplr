package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.data.domain.OrderRepository
import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.domain.OrderStatus
import com.kaaneneskpc.supplr.shared.domain.OrderStatusUpdate
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class OrderRepositoryImpl(private val customerRepository: CustomerRepository) : OrderRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun createTheOrder(
        order: Order,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val orderCollection = database.collection(collectionPath = "order")
                orderCollection.document(order.orderId).set(order)
                customerRepository.deleteAllCartItems(
                    onSuccess = {},
                    onError = {}
                )
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while adding a product to cart: ${e.message}")
        }
    }

    override fun getOrdersByCustomerId(): Flow<RequestState<List<Order>>> = channelFlow {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                database.collection("order")
                    .where { "customerId" equalTo currentUserId }
                    .snapshots
                    .collectLatest { querySnapshot ->
                        val orders = querySnapshot.documents.mapNotNull { document ->
                            try {
                                Order(
                                    orderId = document.id,
                                    customerId = document.get("customerId") ?: "",
                                    items = document.get("items") ?: emptyList(),
                                    totalAmount = document.get("totalAmount") ?: 0.0,
                                    createdAt = document.get("createdAt") ?: 0L,
                                    token = document.get("token"),
                                    currency = document.get("currency") ?: "usd",
                                    paymentIntentId = document.get("paymentIntentId"),
                                    status = document.get("status") ?: "PENDING",
                                    shippingAddress = document.get("shippingAddress") ?: "",
                                    statusHistory = document.get("statusHistory") ?: emptyList(),
                                    estimatedDeliveryDate = document.get("estimatedDeliveryDate"),
                                    trackingNumber = document.get("trackingNumber")
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }.sortedByDescending { it.createdAt }
                        send(RequestState.Success(orders))
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while fetching orders: ${e.message}"))
        }
    }

    override fun getOrderById(orderId: String): Flow<RequestState<Order?>> = channelFlow {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                database.collection("order")
                    .document(orderId)
                    .snapshots
                    .collectLatest { documentSnapshot ->
                        if (documentSnapshot.exists) {
                            try {
                                val order = Order(
                                    orderId = documentSnapshot.id,
                                    customerId = documentSnapshot.get("customerId") ?: "",
                                    items = documentSnapshot.get("items") ?: emptyList(),
                                    totalAmount = documentSnapshot.get("totalAmount") ?: 0.0,
                                    createdAt = documentSnapshot.get("createdAt") ?: 0L,
                                    token = documentSnapshot.get("token"),
                                    currency = documentSnapshot.get("currency") ?: "usd",
                                    paymentIntentId = documentSnapshot.get("paymentIntentId"),
                                    status = documentSnapshot.get("status") ?: "PENDING",
                                    shippingAddress = documentSnapshot.get("shippingAddress") ?: "",
                                    statusHistory = documentSnapshot.get("statusHistory") ?: emptyList(),
                                    estimatedDeliveryDate = documentSnapshot.get("estimatedDeliveryDate"),
                                    trackingNumber = documentSnapshot.get("trackingNumber")
                                )
                                send(RequestState.Success(order))
                            } catch (e: Exception) {
                                send(RequestState.Error("Error parsing order: ${e.message}"))
                            }
                        } else {
                            send(RequestState.Success(null))
                        }
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while fetching order: ${e.message}"))
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        note: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val orderRef = database.collection("order").document(orderId)
                val orderSnapshot = orderRef.get()
                if (orderSnapshot.exists) {
                    val currentStatusHistory: List<OrderStatusUpdate> = 
                        orderSnapshot.get("statusHistory") ?: emptyList()
                    val newStatusUpdate = OrderStatusUpdate(
                        status = newStatus,
                        timestamp = Clock.System.now().toEpochMilliseconds(),
                        note = note
                    )
                    val updatedHistory = currentStatusHistory + newStatusUpdate
                    orderRef.update(
                        "status" to newStatus.name,
                        "statusHistory" to updatedHistory
                    )
                    onSuccess()
                } else {
                    onError("Order not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating order status: ${e.message}")
        }
    }
}