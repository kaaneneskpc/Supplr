package com.kaaneneskpc.supplr.shared.network

import com.kaaneneskpc.supplr.shared.Consts
import com.kaaneneskpc.supplr.shared.domain.PaymentIntentRequest
import com.kaaneneskpc.supplr.shared.domain.PaymentIntentResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class StripeApiClient {
    
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    private companion object {
        const val STRIPE_API_BASE_URL = "https://api.stripe.com/v1"
        const val PAYMENT_INTENTS_ENDPOINT = "$STRIPE_API_BASE_URL/payment_intents"
    }
    
    suspend fun createPaymentIntent(
        amount: Long,
        currency: String = "usd"
    ): Result<PaymentIntentResponse> {
        return try {
            val response = httpClient.submitForm(
                url = PAYMENT_INTENTS_ENDPOINT,
                formParameters = Parameters.build {
                    append("amount", amount.toString())
                    append("currency", currency)
                    append("automatic_payment_methods[enabled]", "true")
                }
            ) {
                header(HttpHeaders.Authorization, "Bearer ${Consts.STRIPE_SECRET_KEY}")
                header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
            
            if (response.status.isSuccess()) {
                val paymentIntent = response.body<PaymentIntentResponse>()
                Result.success(paymentIntent)
            } else {
                Result.failure(Exception("Failed to create payment intent: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun retrievePaymentIntent(paymentIntentId: String): Result<PaymentIntentResponse> {
        return try {
            val response = httpClient.get("$PAYMENT_INTENTS_ENDPOINT/$paymentIntentId") {
                header(HttpHeaders.Authorization, "Bearer ${Consts.STRIPE_SECRET_KEY}")
            }
            
            if (response.status.isSuccess()) {
                val paymentIntent = response.body<PaymentIntentResponse>()
                Result.success(paymentIntent)
            } else {
                Result.failure(Exception("Failed to retrieve payment intent: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun close() {
        httpClient.close()
    }
} 