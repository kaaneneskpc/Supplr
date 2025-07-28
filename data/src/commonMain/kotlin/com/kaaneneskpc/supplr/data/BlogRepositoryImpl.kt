package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.BlogRepository
import com.kaaneneskpc.supplr.shared.domain.Article
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BlogRepositoryImpl : BlogRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun createArticle(
        article: Article,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val firestore = Firebase.firestore
                val blogCollection = firestore.collection("blog")
                val currentTime = Clock.System.now().toEpochMilliseconds()
                
                val articleToCreate = article.copy(
                    createdAt = currentTime,
                    updatedAt = currentTime
                )
                
                blogCollection.document(article.id).set(articleToCreate)
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while creating article: ${e.message}")
        }
    }

    override suspend fun updateArticle(
        article: Article,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val firestore = Firebase.firestore
                val blogCollection = firestore.collection("blog")
                
                val articleToUpdate = article.copy(
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
                
                blogCollection.document(article.id).set(articleToUpdate)
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating article: ${e.message}")
        }
    }

    override suspend fun deleteArticle(
        articleId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val firestore = Firebase.firestore
                val blogCollection = firestore.collection("blog")
                
                // Delete article document
                blogCollection.document(articleId).delete()
                
                // Delete associated image from storage
                try {
                    val storage = Firebase.storage
                    val imageRef = storage.reference.child("blog_images").child("$articleId.jpg")
                    imageRef.delete()
                } catch (e: Exception) {
                    // Image might not exist, continue with success
                }
                
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while deleting article: ${e.message}")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun uploadArticleImage(
        articleId: String,
        imageFile: File,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val storage = Firebase.storage
                val fileName = "${articleId}_${Uuid.random()}.jpg"
                val imageRef = storage.reference.child("blog_images").child(fileName)
                
                withTimeout(timeMillis = 10000) {
                    imageRef.putFile(imageFile)
                }
                
                val downloadUrl = imageRef.getDownloadUrl()
                onSuccess(downloadUrl)
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while uploading image: ${e.message}")
        }
    }

    override fun getAllArticles(): Flow<RequestState<List<Article>>> = channelFlow {
        try {
            send(RequestState.Loading)
            val firestore = Firebase.firestore
            val blogCollection = firestore.collection("blog")
            
            blogCollection
                .orderBy("createdAt", Direction.DESCENDING)
                .snapshots
                .collectLatest { snapshot ->
                    try {
                        val articles = snapshot.documents.map { document ->
                            document.data<Article>()
                        }
                        send(RequestState.Success(articles))
                    } catch (e: Exception) {
                        send(RequestState.Error("Error while parsing articles: ${e.message}"))
                    }
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading articles: ${e.message}"))
        }
    }

    override fun getArticleById(articleId: String): Flow<RequestState<Article?>> = channelFlow {
        try {
            send(RequestState.Loading)
            val firestore = Firebase.firestore
            val blogCollection = firestore.collection("blog")
            
            blogCollection.document(articleId)
                .snapshots
                .collectLatest { snapshot ->
                    try {
                        if (snapshot.exists) {
                            val article = snapshot.data<Article>()
                            send(RequestState.Success(article))
                        } else {
                            send(RequestState.Success(null))
                        }
                    } catch (e: Exception) {
                        send(RequestState.Error("Error while parsing article: ${e.message}"))
                    }
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading article: ${e.message}"))
        }
    }

    override fun getArticlesByCategory(category: String): Flow<RequestState<List<Article>>> = channelFlow {
        try {
            send(RequestState.Loading)
            val firestore = Firebase.firestore
            val blogCollection = firestore.collection("blog")

            blogCollection
                .where { "category" equalTo category }
                .orderBy("createdAt", Direction.DESCENDING)
                .snapshots
                .collectLatest { snapshot ->
                    try {
                        val articles = snapshot.documents.map { document ->
                            document.data<Article>()
                        }
                        send(RequestState.Success(articles))
                    } catch (e: Exception) {
                        send(RequestState.Error("Error while parsing articles: ${e.message}"))
                    }
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading articles by category: ${e.message}"))
        }
    }

    override fun searchArticles(query: String): Flow<RequestState<List<Article>>> = channelFlow {
        try {
            send(RequestState.Loading)
            val firestore = Firebase.firestore
            val blogCollection = firestore.collection("blog")
            
            // Firebase doesn't support full-text search, so we'll get all articles and filter client-side
            blogCollection
                .orderBy("createdAt", Direction.DESCENDING)
                .snapshots
                .collectLatest { snapshot ->
                    try {
                        val allArticles = snapshot.documents.map { document ->
                            document.data<Article>()
                        }
                        
                        val filteredArticles = allArticles.filter { article ->
                            article.title.contains(query, ignoreCase = true) ||
                            article.subtitle.contains(query, ignoreCase = true) ||
                            article.content.contains(query, ignoreCase = true) ||
                            article.tags.any { tag -> tag.contains(query, ignoreCase = true) }
                        }
                        
                        send(RequestState.Success(filteredArticles))
                    } catch (e: Exception) {
                        send(RequestState.Error("Error while searching articles: ${e.message}"))
                    }
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error while searching articles: ${e.message}"))
        }
    }
} 