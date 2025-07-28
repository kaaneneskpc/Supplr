package com.kaaneneskpc.supplr.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor

@Composable
fun RatingStars(
    rating: Float,
    maxRating: Int = 5,
    size: Dp = 20.dp,
    activeColor: Color = Color(0xFFFFD700),
    inactiveColor: Color = Color(0xFFE0E0E0),
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        repeat(maxRating) { index ->
            val starRating = rating - index
            
            when {
                starRating >= 1f -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Full star",
                        tint = activeColor,
                        modifier = Modifier.size(size)
                    )
                }
                starRating >= 0.5f -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Half star",
                        tint = activeColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(size)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Empty star",
                        tint = inactiveColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(size)
                    )
                }
            }
        }
    }
} 