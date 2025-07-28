package com.kaaneneskpc.supplr.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val author: String = "",
    val authorTitle: String = "",
    val publishDate: String = "",
    val readTimeMinutes: Int = 0,
    val category: ArticleCategory = ArticleCategory.LIFESTYLE,
    val tags: List<String> = emptyList(),
    val isPublished: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
enum class ArticleCategory(val displayName: String) {
    NUTRITION("Beslenme"),
    FITNESS("Spor"),
    WELLNESS("Sağlıklı Yaşam"),
    RECIPES("Tarifler"),
    LIFESTYLE("Yaşam Tarzı")
} 