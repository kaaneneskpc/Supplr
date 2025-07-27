package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.PaymentRepository
import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.domain.PaymentIntentResponse
import com.kaaneneskpc.supplr.shared.network.StripeApiClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PaymentRepositoryImpl : PaymentRepository {
    
    private val stripeApiClient = StripeApiClient()
    
    override suspend fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }
    
    override suspend fun createPaymentIntent(
        amount: Long,
        currency: String
    ): Result<PaymentIntentResponse> {
        return stripeApiClient.createPaymentIntent(amount, currency)
    }
    
    override suspend fun getPaymentIntentStatus(paymentIntentId: String): Result<PaymentIntentResponse> {
        return stripeApiClient.retrievePaymentIntent(paymentIntentId)
    }
    
    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun saveOrder(order: Order): Result<String> {
        return try {
            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val database = Firebase.firestore
            val orderCollection = database.collection("order")
            
            val orderId = if (order.orderId.isEmpty()) Uuid.random().toString() else order.orderId
            val orderWithId = order.copy(
                orderId = orderId,
                customerId = currentUserId
            )
            
            orderCollection.document(orderId).set(orderWithId)
            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOrdersForUser(userId: String): Result<List<Order>> {
        return try {
            val database = Firebase.firestore
            val querySnapshot = database.collection("order")
                .where { "customerId" equalTo userId }
                .get()
            
            val orders = querySnapshot.documents.map { document ->
                Order(
                    orderId = document.id,
                    customerId = document.get("customerId") ?: "",
                    items = document.get("items") ?: emptyList(),
                    totalAmount = document.get("totalAmount") ?: 0.0,
                    token = document.get("token"),
                    currency = document.get("currency") ?: "usd",
                    paymentIntentId = document.get("paymentIntentId"),
                    status = document.get("status") ?: "PENDING",
                    shippingAddress = document.get("shippingAddress") ?: ""
                )
            }
            
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val database = Firebase.firestore
            val document = database.collection("order").document(orderId).get()
            
            if (document.exists) {
                val order = Order(
                    orderId = document.id,
                    customerId = document.get("customerId") ?: "",
                    items = document.get("items") ?: emptyList(),
                    totalAmount = document.get("totalAmount") ?: 0.0,
                    token = document.get("token"),
                    currency = document.get("currency") ?: "usd",
                    paymentIntentId = document.get("paymentIntentId"),
                    status = document.get("status") ?: "PENDING",
                    shippingAddress = document.get("shippingAddress") ?: ""
                )
                Result.success(order)
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            val database = Firebase.firestore
            database.collection("order").document(orderId).update(
                mapOf("status" to status)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 