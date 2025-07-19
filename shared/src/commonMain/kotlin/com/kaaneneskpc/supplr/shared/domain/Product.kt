package com.kaaneneskpc.supplr.shared.domain

import androidx.compose.ui.graphics.Color
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlue
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlueLighter
import com.kaaneneskpc.supplr.shared.fonts.CategoryGreen
import com.kaaneneskpc.supplr.shared.fonts.CategoryPurple
import com.kaaneneskpc.supplr.shared.fonts.CategoryRed
import com.kaaneneskpc.supplr.shared.fonts.CategoryYellow
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class Product(
    val id: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val title: String,
    val description: String,
    val thumbnail: String,
    val category: String,
    val flavors: List<String>? = null,
    val weight: Int? = null,
    val price: Double,
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false,
    val isNew: Boolean = false,
)

enum class ProductCategory(
    val title: String,
    val color: Color
) {
    Meat(
        title = "Meat",
        color = CategoryYellow
    ),
    Chicken(
        title = "Chicken",
        color = CategoryBlue
    ),
    FastFood(
        title = "FastFood",
        color = CategoryGreen
    ),
    Vegetarian(
        title = "Vegetarian",
        color = CategoryPurple
    ),
    SaladMixed(
        title = "SaladMixed",
        color = CategoryRed
    ),
    Others(
    title = "Others",
    color = CategoryBlueLighter
    )
}
