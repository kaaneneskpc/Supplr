package com.kaaneneskpc.supplr.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.domain.Review
import com.kaaneneskpc.supplr.shared.fonts.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier
) {
    // Animation for review appearance
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = SurfaceBrand.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SurfaceBrand.copy(alpha = 0.08f),
                            Surface,
                            SurfaceLighter.copy(alpha = 0.3f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with user info and enhanced rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User info section
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // User avatar placeholder
                            Surface(
                                color = SurfaceBrand.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = review.username.take(1).uppercase(),
                                        fontFamily = BebasNeueFont(),
                                        fontSize = FontSize.MEDIUM,
                                        fontWeight = FontWeight.Bold,
                                        color = SurfaceBrand
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = "👤 ${review.username}",
                                    fontFamily = RobotoCondensedFont(),
                                    fontSize = FontSize.MEDIUM,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "📅 ${formatReviewDate(review.createdAt)}",
                                    fontFamily = RobotoCondensedFont(),
                                    fontSize = FontSize.SMALL,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                    
                    // Enhanced rating badge
                    EnhancedRatingBadge(rating = review.rating)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Review comment with enhanced styling
                if (review.comment.isNotBlank()) {
                    Surface(
                        color = Surface,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "💭 Review",
                                fontFamily = BebasNeueFont(),
                                fontSize = FontSize.SMALL,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Text(
                                text = "\"${review.comment}\"",
                                fontFamily = RobotoCondensedFont(),
                                fontSize = FontSize.MEDIUM,
                                color = TextPrimary,
                                lineHeight = FontSize.MEDIUM * 1.5
                            )
                        }
                    }
                }
                
                // Enhanced badges section
                if (review.isVerifiedPurchase || review.rating >= 4.0f) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Verified purchase badge
                        if (review.isVerifiedPurchase) {
                            EnhancedBadge(
                                text = "✅ Verified Purchase",
                                backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.15f),
                                textColor = Color(0xFF2E7D32)
                            )
                        }
                        
                        // High rating badge
                        if (review.rating >= 4.0f) {
                            EnhancedBadge(
                                text = "⭐ Recommended",
                                backgroundColor = SurfaceBrand.copy(alpha = 0.15f),
                                textColor = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedRatingBadge(
    rating: Float,
    modifier: Modifier = Modifier
) {
    val ratingColor = when {
        rating >= 4.5f -> Color(0xFF4CAF50)
        rating >= 3.5f -> Color(0xFFFF9800)
        rating >= 2.5f -> Color(0xFFFF5722)
        else -> Color(0xFFF44336)
    }
    
    val ratingEmoji = when {
        rating >= 4.5f -> "🤩"
        rating >= 3.5f -> "😊"
        rating >= 2.5f -> "😐"
        else -> "😞"
    }
    
    Surface(
        color = ratingColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RatingStars(
                    rating = rating,
                    size = 16.dp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = ratingEmoji,
                    fontSize = FontSize.SMALL
                )
                Text(
                    text = "${(rating * 10).toInt() / 10.0}",
                    fontFamily = RobotoCondensedFont(),
                    fontSize = FontSize.SMALL,
                    fontWeight = FontWeight.Bold,
                    color = ratingColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EnhancedBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontFamily = RobotoCondensedFont(),
            fontSize = FontSize.EXTRA_SMALL,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
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