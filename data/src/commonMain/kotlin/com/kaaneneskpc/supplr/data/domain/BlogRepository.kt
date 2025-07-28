package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Article
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.Flow

interface BlogRepository {
    fun getCurrentUserId(): String?
    
    // Article management
    suspend fun createArticle(
        article: Article,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    suspend fun updateArticle(
        article: Article,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    suspend fun deleteArticle(
        articleId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    // Image upload
    suspend fun uploadArticleImage(
        articleId: String,
        imageFile: File,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )
    
    // Read operations
    fun getAllArticles(): Flow<RequestState<List<Article>>>
    fun getArticleById(articleId: String): Flow<RequestState<Article?>>
    fun getArticlesByCategory(category: String): Flow<RequestState<List<Article>>>
    fun searchArticles(query: String): Flow<RequestState<List<Article>>>
} 