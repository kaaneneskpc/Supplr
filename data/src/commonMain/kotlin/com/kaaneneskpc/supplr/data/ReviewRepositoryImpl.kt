package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.ReviewRepository
import com.kaaneneskpc.supplr.shared.domain.Review
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ReviewRepositoryImpl : ReviewRepository {
    
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override fun getReviewsForProduct(productId: String): Flow<RequestState<List<Review>>> = channelFlow {
        try {
            send(RequestState.Loading)
            val database = Firebase.firestore
            database.collection("reviews")
                .where { "productId" equalTo productId }
                .snapshots
                .collectLatest { query ->
                    val reviews = query.documents.map { document ->
                        Review(
                            id = document.id,
                            productId = document.get("productId"),
                            userId = document.get("userId"),
                            username = document.get("username"),
                            rating = document.get("rating"),
                            comment = document.get("comment"),
                            createdAt = document.get("createdAt"),
                            isVerifiedPurchase = document.get("isVerifiedPurchase") ?: false,
                            isApproved = document.get("isApproved") ?: true
                        )
                    }
                    // Filter and sort in code temporarily
                    val filteredReviews = reviews
                        .filter { it.isApproved }
                        .sortedByDescending { it.createdAt }
                    send(RequestState.Success(filteredReviews))
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error while fetching reviews: ${e.message}"))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addReview(
        productId: String,
        rating: Float,
        comment: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val currentUser = Firebase.auth.currentUser
                val username = currentUser?.displayName ?: "Anonymous User"
                
                // Allow multiple reviews from same user
                
                val reviewId = Uuid.random().toString()
                val review = Review(
                    id = reviewId,
                    productId = productId,
                    userId = currentUserId,
                    username = username,
                    rating = rating,
                    comment = comment,
                    isVerifiedPurchase = false,
                    isApproved = true // Auto-approve for now
                )
                
                val database = Firebase.firestore
                database.collection("reviews")
                    .document(reviewId)
                    .set(review)
                
                onSuccess()
            } else {
                onError("User is not authenticated.")
            }
        } catch (e: Exception) {
            onError("Error while adding review: ${e.message}")
        }
    }

    override suspend fun updateReview(
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null && currentUserId == review.userId) {
                val database = Firebase.firestore
                database.collection("reviews")
                    .document(review.id)
                    .set(review)
                onSuccess()
            } else {
                onError("You can only update your own reviews.")
            }
        } catch (e: Exception) {
            onError("Error while updating review: ${e.message}")
        }
    }

    override suspend fun deleteReview(
        reviewId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val reviewDoc = database.collection("reviews").document(reviewId)
                val review = reviewDoc.get()
                
                val reviewUserId: String = review.get("userId")
                if (currentUserId == reviewUserId) {
                    reviewDoc.delete()
                    onSuccess()
                } else {
                    onError("You can only delete your own reviews.")
                }
            } else {
                onError("User is not authenticated.")
            }
        } catch (e: Exception) {
            onError("Error while deleting review: ${e.message}")
        }
    }

    override suspend fun getProductAverageRating(productId: String): RequestState<Float> {
        return try {
            val database = Firebase.firestore
            val query = database.collection("reviews")
                .where { "productId" equalTo productId }
                .where { "isApproved" equalTo true }
                .get()
            
            val reviews = query.documents.map { document ->
                document.get<Float>("rating")
            }
            
            if (reviews.isNotEmpty()) {
                val averageRating = reviews.average().toFloat()
                RequestState.Success(averageRating)
            } else {
                RequestState.Success(0f)
            }
        } catch (e: Exception) {
            RequestState.Error("Error while calculating average rating: ${e.message}")
        }
    }

    override suspend fun getProductReviewCount(productId: String): RequestState<Int> {
        return try {
            val database = Firebase.firestore
            val query = database.collection("reviews")
                .where { "productId" equalTo productId }
                .where { "isApproved" equalTo true }
                .get()
            
            RequestState.Success(query.documents.size)
        } catch (e: Exception) {
            RequestState.Error("Error while counting reviews: ${e.message}")
        }
    }

    override suspend fun hasUserReviewedProduct(productId: String): RequestState<Boolean> {
        return try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val query = database.collection("reviews")
                    .where { "productId" equalTo productId }
                    .where { "userId" equalTo currentUserId }
                    .get()
                
                RequestState.Success(query.documents.isNotEmpty())
            } else {
                RequestState.Error("User is not authenticated.")
            }
        } catch (e: Exception) {
            RequestState.Error("Error while checking user review: ${e.message}")
        }
    }
} 