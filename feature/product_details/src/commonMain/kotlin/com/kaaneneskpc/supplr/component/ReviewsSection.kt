package com.kaaneneskpc.supplr.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.domain.Review
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.RobotoCondensedFont
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextSecondary
import com.kaaneneskpc.supplr.shared.util.RequestState

@Composable
fun ReviewsSection(
    reviewsState: RequestState<List<Review>>,
    averageRating: Float,
    reviewCount: Int,
    hasUserReviewed: Boolean,
    onWriteReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header with rating summary
        ReviewsSummaryHeader(
            averageRating = averageRating,
            reviewCount = reviewCount,
            hasUserReviewed = hasUserReviewed,
            onWriteReviewClick = onWriteReviewClick
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Reviews list with enhanced spacing
        when (reviewsState) {
            is RequestState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator()
                        Text(
                            text = "Loading reviews...",
                            fontFamily = RobotoCondensedFont(),
                            fontSize = FontSize.SMALL,
                            color = TextSecondary
                        )
                    }
                }
            }
            is RequestState.Success -> {
                if (reviewsState.data.isEmpty()) {
                    EmptyReviewsMessage()
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Reviews count header
                        Text(
                            text = "📝 Customer Reviews (${reviewsState.data.size})",
                            fontFamily = RobotoCondensedFont(),
                            fontSize = FontSize.MEDIUM,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        
                        // Reviews list with enhanced spacing
                        reviewsState.data.forEach { review ->
                            ReviewItem(review = review)
                        }
                    }
                }
            }
            is RequestState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "😔 Unable to load reviews",
                            fontFamily = RobotoCondensedFont(),
                            fontSize = FontSize.MEDIUM,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error
                        )
                Text(
                            text = reviewsState.message,
                            fontFamily = RobotoCondensedFont(),
                            fontSize = FontSize.SMALL,
                            color = TextSecondary
                )
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun ReviewsSummaryHeader(
    averageRating: Float,
    reviewCount: Int,
    hasUserReviewed: Boolean,
    onWriteReviewClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingStars(rating = averageRating)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(averageRating * 10).toInt() / 10.0}",
                    fontFamily = RobotoCondensedFont(),
                    fontSize = FontSize.LARGE,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Text(
                text = "$reviewCount ${if (reviewCount == 1) "review" else "reviews"}",
                fontFamily = RobotoCondensedFont(),
                fontSize = FontSize.MEDIUM,
                color = TextSecondary
            )
        }
        
        FilledTonalButton(
            onClick = onWriteReviewClick,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "✍️ Write Review",
                fontFamily = RobotoCondensedFont(),
                fontSize = FontSize.MEDIUM
            )
        }
    }
}

@Composable
private fun EmptyReviewsMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📝",
                fontSize = FontSize.EXTRA_LARGE
            )
        Text(
                text = "No reviews yet",
            fontFamily = RobotoCondensedFont(),
            fontSize = FontSize.MEDIUM,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = "Be the first to review this product!",
                fontFamily = RobotoCondensedFont(),
                fontSize = FontSize.SMALL,
            color = TextSecondary
        )
        }
    }
} 