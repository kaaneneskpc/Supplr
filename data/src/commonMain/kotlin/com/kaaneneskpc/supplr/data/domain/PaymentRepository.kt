package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.domain.PaymentIntentResponse
import com.kaaneneskpc.supplr.shared.domain.PaymentResult

interface PaymentRepository {
    
    /**
     * Creates a payment intent with Stripe
     */
    suspend fun createPaymentIntent(
        amount: Long,
        currency: String = "usd"
    ): Result<PaymentIntentResponse>
    
    /**
     * Retrieves payment intent status from Stripe
     */
    suspend fun getPaymentIntentStatus(paymentIntentId: String): Result<PaymentIntentResponse>
    
    /**
     * Saves order after successful payment
     */
    suspend fun saveOrder(order: Order): Result<String>
    
    /**
     * Gets orders for a specific user
     */
    suspend fun getOrdersForUser(userId: String): Result<List<Order>>
    
    /**
     * Gets order by ID
     */
    suspend fun getOrderById(orderId: String): Result<Order>
    
    /**
     * Updates order status
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit>
    
    /**
     * Gets current authenticated user ID
     */
    suspend fun getCurrentUserId(): String?
} 