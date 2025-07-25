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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Reviews list
        when (reviewsState) {
            is RequestState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RequestState.Success -> {
                if (reviewsState.data.isEmpty()) {
                    EmptyReviewsMessage()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(reviewsState.data) { review ->
                            ReviewItem(review = review)
                        }
                    }
                }
            }
            is RequestState.Error -> {
                Text(
                    text = "Error loading reviews: ${reviewsState.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
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
            onClick = onWriteReviewClick
        ) {
            Text(
                text = "Write Review",
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
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No reviews yet. Be the first to review!",
            fontFamily = RobotoCondensedFont(),
            fontSize = FontSize.MEDIUM,
            color = TextSecondary
        )
    }
} 