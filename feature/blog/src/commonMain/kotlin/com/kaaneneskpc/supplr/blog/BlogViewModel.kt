package com.kaaneneskpc.supplr.blog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.BlogRepository
import com.kaaneneskpc.supplr.shared.domain.Article
import com.kaaneneskpc.supplr.shared.domain.ArticleCategory
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BlogViewModel(
    private val blogRepository: BlogRepository
) : ViewModel() {

    private val _articles = MutableStateFlow<RequestState<List<Article>>>(RequestState.Loading)
    val articles: StateFlow<RequestState<List<Article>>> = _articles.asStateFlow()

    private val _selectedArticle = MutableStateFlow<RequestState<Article?>>(RequestState.Loading)
    val selectedArticle: StateFlow<RequestState<Article?>> = _selectedArticle.asStateFlow()

    // Article form state
    var articleFormState by mutableStateOf(ArticleFormState())
        private set

    init {
        loadAllArticles()
    }

    fun loadAllArticles() {
        viewModelScope.launch {
            blogRepository.getAllArticles().collect { result ->
                _articles.value = result
            }
        }
    }

    fun loadArticleById(articleId: String) {
        viewModelScope.launch {
            blogRepository.getArticleById(articleId).collect { result ->
                _selectedArticle.value = result
            }
        }
    }

    fun loadArticlesByCategory(category: ArticleCategory) {
        viewModelScope.launch {
            blogRepository.getArticlesByCategory(category.name).collect { result ->
                _articles.value = result
            }
        }
    }

    fun searchArticles(query: String) {
        if (query.isBlank()) {
            loadAllArticles()
        } else {
            viewModelScope.launch {
                blogRepository.searchArticles(query).collect { result ->
                    _articles.value = result
                }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun createArticle(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val articleId = Uuid.random().toString()
                val article = Article(
                    id = articleId,
                    title = articleFormState.title,
                    subtitle = articleFormState.subtitle,
                    content = articleFormState.content,
                    imageUrl = articleFormState.imageUrl,
                    author = articleFormState.author,
                    authorTitle = articleFormState.authorTitle,
                    publishDate = getCurrentDate(),
                    readTimeMinutes = calculateReadTime(articleFormState.content),
                    category = articleFormState.category,
                    tags = articleFormState.tags,
                    isPublished = articleFormState.isPublished
                )

                blogRepository.createArticle(
                    article = article,
                    onSuccess = {
                        resetForm()
                        loadAllArticles()
                        onSuccess()
                    },
                    onError = onError
                )
            } catch (e: Exception) {
                onError("Error creating article: ${e.message}")
            }
        }
    }

    fun updateArticle(
        articleId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val article = Article(
                    id = articleId,
                    title = articleFormState.title,
                    subtitle = articleFormState.subtitle,
                    content = articleFormState.content,
                    imageUrl = articleFormState.imageUrl,
                    author = articleFormState.author,
                    authorTitle = articleFormState.authorTitle,
                    publishDate = articleFormState.publishDate.takeIf { it.isNotBlank() } ?: getCurrentDate(),
                    readTimeMinutes = calculateReadTime(articleFormState.content),
                    category = articleFormState.category,
                    tags = articleFormState.tags,
                    isPublished = articleFormState.isPublished
                )

                blogRepository.updateArticle(
                    article = article,
                    onSuccess = {
                        resetForm()
                        loadAllArticles()
                        onSuccess()
                    },
                    onError = onError
                )
            } catch (e: Exception) {
                onError("Error updating article: ${e.message}")
            }
        }
    }

    fun deleteArticle(
        articleId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            blogRepository.deleteArticle(
                articleId = articleId,
                onSuccess = {
                    loadAllArticles()
                    onSuccess()
                },
                onError = onError
            )
        }
    }

    fun uploadImage(
        articleId: String,
        imageFile: File,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            blogRepository.uploadArticleImage(
                articleId = articleId,
                imageFile = imageFile,
                onSuccess = { imageUrl ->
                    updateImageUrl(imageUrl)
                    onSuccess(imageUrl)
                },
                onError = onError
            )
        }
    }

    // Form state management
    fun updateTitle(title: String) {
        articleFormState = articleFormState.copy(title = title)
    }

    fun updateSubtitle(subtitle: String) {
        articleFormState = articleFormState.copy(subtitle = subtitle)
    }

    fun updateContent(content: String) {
        articleFormState = articleFormState.copy(content = content)
    }

    fun updateAuthor(author: String) {
        articleFormState = articleFormState.copy(author = author)
    }

    fun updateAuthorTitle(authorTitle: String) {
        articleFormState = articleFormState.copy(authorTitle = authorTitle)
    }

    fun updateCategory(category: ArticleCategory) {
        articleFormState = articleFormState.copy(category = category)
    }

    fun updateTags(tags: List<String>) {
        articleFormState = articleFormState.copy(tags = tags)
    }

    fun updateIsPublished(isPublished: Boolean) {
        articleFormState = articleFormState.copy(isPublished = isPublished)
    }

    private fun updateImageUrl(imageUrl: String) {
        articleFormState = articleFormState.copy(imageUrl = imageUrl)
    }

    fun loadArticleForEditing(article: Article) {
        articleFormState = ArticleFormState(
            title = article.title,
            subtitle = article.subtitle,
            content = article.content,
            imageUrl = article.imageUrl,
            author = article.author,
            authorTitle = article.authorTitle,
            publishDate = article.publishDate,
            category = article.category,
            tags = article.tags,
            isPublished = article.isPublished
        )
    }

    fun resetForm() {
        articleFormState = ArticleFormState()
    }

    fun isFormValid(): Boolean {
        return articleFormState.title.isNotBlank() &&
                articleFormState.subtitle.isNotBlank() &&
                articleFormState.content.isNotBlank() &&
                articleFormState.author.isNotBlank()
    }

    private fun getCurrentDate(): String {
        // Simple date formatting - in production, use proper date formatting
        return "Today" // You can implement proper date formatting here
    }

    private fun calculateReadTime(content: String): Int {
        // Average reading speed is ~200 words per minute
        val wordCount = content.split("\\s+".toRegex()).size
        return maxOf(1, (wordCount / 200))
    }
}

data class ArticleFormState(
    val title: String = "",
    val subtitle: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val author: String = "",
    val authorTitle: String = "",
    val publishDate: String = "",
    val category: ArticleCategory = ArticleCategory.LIFESTYLE,
    val tags: List<String> = emptyList(),
    val isPublished: Boolean = false
) 