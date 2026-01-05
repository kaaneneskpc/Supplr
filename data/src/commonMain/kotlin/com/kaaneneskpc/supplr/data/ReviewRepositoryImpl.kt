package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.ReviewRepository
import com.kaaneneskpc.supplr.shared.domain.Review
import com.kaaneneskpc.supplr.shared.domain.ReviewVote
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
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
                            photoUrls = document.get("photoUrls") ?: emptyList(),
                            helpfulCount = document.get("helpfulCount") ?: 0,
                            unhelpfulCount = document.get("unhelpfulCount") ?: 0,
                            createdAt = document.get("createdAt"),
                            isVerifiedPurchase = document.get("isVerifiedPurchase") ?: false,
                            isApproved = document.get("isApproved") ?: true
                        )
                    }
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
        photoUrls: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val currentUser = Firebase.auth.currentUser
                val username = currentUser?.displayName ?: "Anonymous User"
                val reviewId = Uuid.random().toString()
                val review = Review(
                    id = reviewId,
                    productId = productId,
                    userId = currentUserId,
                    username = username,
                    rating = rating,
                    comment = comment,
                    photoUrls = photoUrls,
                    helpfulCount = 0,
                    unhelpfulCount = 0,
                    isVerifiedPurchase = false,
                    isApproved = true
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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun uploadReviewPhoto(
        file: dev.gitlive.firebase.storage.File,
        reviewId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val storage = Firebase.storage
                val photoId = Uuid.random().toString()
                val photoRef = storage.reference.child("review_photos/$reviewId/$photoId.jpg")
                photoRef.putFile(file)
                val downloadUrl = photoRef.getDownloadUrl()
                onSuccess(downloadUrl)
            } else {
                onError("User is not authenticated.")
            }
        } catch (e: Exception) {
            onError("Error while uploading photo: ${e.message}")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun voteReview(
        reviewId: String,
        isHelpful: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                onError("User is not authenticated.")
                return
            }
            val database = Firebase.firestore
            val reviewDoc = database.collection("reviews").document(reviewId)
            val review = reviewDoc.get()
            val reviewUserId: String = review.get("userId")
            if (currentUserId == reviewUserId) {
                onError("You cannot vote on your own review.")
                return
            }
            val existingVoteQuery = database.collection("review_votes")
                .where { "reviewId" equalTo reviewId }
                .where { "userId" equalTo currentUserId }
                .get()
            if (existingVoteQuery.documents.isNotEmpty()) {
                val existingVote = existingVoteQuery.documents.first()
                val existingIsHelpful: Boolean = existingVote.get("isHelpful")
                if (existingIsHelpful == isHelpful) {
                    onError("You have already voted on this review.")
                    return
                }
                database.collection("review_votes").document(existingVote.id).delete()
                if (existingIsHelpful) {
                    reviewDoc.update("helpfulCount" to FieldValue.increment(-1))
                } else {
                    reviewDoc.update("unhelpfulCount" to FieldValue.increment(-1))
                }
            }
            val voteId = Uuid.random().toString()
            val vote = ReviewVote(
                id = voteId,
                reviewId = reviewId,
                userId = currentUserId,
                isHelpful = isHelpful
            )
            database.collection("review_votes").document(voteId).set(vote)
            if (isHelpful) {
                reviewDoc.update("helpfulCount" to FieldValue.increment(1))
            } else {
                reviewDoc.update("unhelpfulCount" to FieldValue.increment(1))
            }
            onSuccess()
        } catch (e: Exception) {
            onError("Error while voting: ${e.message}")
        }
    }

    override suspend fun getUserVoteForReview(reviewId: String): RequestState<Boolean?> {
        return try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val query = database.collection("review_votes")
                    .where { "reviewId" equalTo reviewId }
                    .where { "userId" equalTo currentUserId }
                    .get()
                if (query.documents.isNotEmpty()) {
                    val isHelpful: Boolean = query.documents.first().get("isHelpful")
                    RequestState.Success(isHelpful)
                } else {
                    RequestState.Success(null)
                }
            } else {
                RequestState.Error("User is not authenticated.")
            }
        } catch (e: Exception) {
            RequestState.Error("Error while checking vote: ${e.message}")
        }
    }

    override suspend fun removeVote(
        reviewId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                onError("User is not authenticated.")
                return
            }
            val database = Firebase.firestore
            val existingVoteQuery = database.collection("review_votes")
                .where { "reviewId" equalTo reviewId }
                .where { "userId" equalTo currentUserId }
                .get()
            if (existingVoteQuery.documents.isEmpty()) {
                onError("No vote found to remove.")
                return
            }
            val existingVote = existingVoteQuery.documents.first()
            val existingIsHelpful: Boolean = existingVote.get("isHelpful")
            database.collection("review_votes").document(existingVote.id).delete()
            val reviewDoc = database.collection("reviews").document(reviewId)
            if (existingIsHelpful) {
                reviewDoc.update("helpfulCount" to FieldValue.increment(-1))
            } else {
                reviewDoc.update("unhelpfulCount" to FieldValue.increment(-1))
            }
            onSuccess()
        } catch (e: Exception) {
            onError("Error while removing vote: ${e.message}")
        }
    }
}