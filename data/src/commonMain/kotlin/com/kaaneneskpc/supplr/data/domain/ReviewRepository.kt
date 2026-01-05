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
        photoUrls: List<String> = emptyList(),
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
    
    /**
     * Uploads a review photo to Firebase Storage
     * @param file The file to upload
     * @param reviewId The review ID to associate with the photo
     * @return The download URL of the uploaded photo
     */
    suspend fun uploadReviewPhoto(
        file: dev.gitlive.firebase.storage.File,
        reviewId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Vote on a review as helpful or unhelpful
     * @param reviewId The ID of the review to vote on
     * @param isHelpful True for helpful vote, false for unhelpful vote
     */
    suspend fun voteReview(
        reviewId: String,
        isHelpful: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Check if the current user has voted on a review
     * @return null if not voted, true if voted helpful, false if voted unhelpful
     */
    suspend fun getUserVoteForReview(reviewId: String): RequestState<Boolean?>
    
    /**
     * Remove user's vote from a review
     */
    suspend fun removeVote(
        reviewId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
} 