package com.kaaneneskpc.supplr.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.domain.Review
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.RobotoCondensedFont
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextSecondary
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with user info and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = review.username,
                        fontFamily = RobotoCondensedFont(),
                        fontSize = FontSize.MEDIUM,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = formatReviewDate(review.createdAt),
                        fontFamily = RobotoCondensedFont(),
                        fontSize = FontSize.SMALL,
                        color = TextSecondary
                    )
                }
                
                RatingStars(
                    rating = review.rating,
                    size = 16.dp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Review comment
            if (review.comment.isNotBlank()) {
                Text(
                    text = review.comment,
                    fontFamily = RobotoCondensedFont(),
                    fontSize = FontSize.MEDIUM,
                    color = TextPrimary,
                    lineHeight = FontSize.MEDIUM * 1.4
                )
            }
            
            // Verified purchase badge
            if (review.isVerifiedPurchase) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "Verified Purchase",
                            fontFamily = RobotoCondensedFont(),
                            fontSize = FontSize.SMALL
                        )
                    }
                }
            }
        }
    }
}

private fun formatReviewDate(timestamp: Long): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}"
    } catch (e: Exception) {
        "Unknown date"
    }
} 