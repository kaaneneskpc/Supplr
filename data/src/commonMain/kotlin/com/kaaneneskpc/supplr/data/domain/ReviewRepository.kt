package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Review
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getCurrentUserId(): String?
    
    /**
     * Fetches all reviews for a specific product
     */
    fun getReviewsForProduct(productId: String): Flow<RequestState<List<Review>>>
    
    /**
     * Adds a new review for a product
     */
    suspend fun addReview(
        productId: String,
        rating: Float,
        comment: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Updates an existing review
     */
    suspend fun updateReview(
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Deletes a review
     */
    suspend fun deleteReview(
        reviewId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Gets the average rating for a product
     */
    suspend fun getProductAverageRating(productId: String): RequestState<Float>
    
    /**
     * Gets the total number of reviews for a product
     */
    suspend fun getProductReviewCount(productId: String): RequestState<Int>
    
    /**
     * Checks if the current user has already reviewed this product
     */
    suspend fun hasUserReviewedProduct(productId: String): RequestState<Boolean>
} 