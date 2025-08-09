package com.kaaneneskpc.supplr

import ContentWithMessageBar
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.component.SupplrButton
import com.kaaneneskpc.supplr.shared.fonts.*
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun AddReviewScreen(
    productId: String,
    navigateBack: () -> Unit
) {
    val messageBarState = rememberMessageBarState()
    val productDetailViewModel = koinViewModel<ProductDetailViewModel>()

    var rating by remember { mutableFloatStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val animatedVisibility by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = EaseOutCubic)
    )

    CommonScaffold(
        title = "Write Review",
        navigateBack = navigateBack
    ) { paddingValues ->
        ContentWithMessageBar(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            contentBackgroundColor = Surface,
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Surface,
                                SurfaceLighter,
                                Surface
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .scale(animatedVisibility),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = SurfaceBrand.copy(alpha = 0.25f)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Surface
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                SurfaceBrand.copy(alpha = 0.1f),
                                                Surface,
                                                SurfaceBrand.copy(alpha = 0.05f)
                                            )
                                        )
                                    )
                                    .padding(24.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "✨ Rate this product",
                                        fontSize = FontSize.EXTRA_LARGE,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = BebasNeueFont(),
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Your opinion matters to other customers",
                                        fontSize = FontSize.SMALL,
                                        fontFamily = RobotoCondensedFont(),
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    EnhancedRatingStars(
                                        rating = rating,
                                        onRatingChange = { rating = it },
                                        size = 36.dp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    AnimatedRatingText(rating = rating)
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = TextSecondary.copy(alpha = 0.2f)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "💭 Share your thoughts",
                                        fontSize = FontSize.LARGE,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = BebasNeueFont(),
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Surface(
                                        color = if (comment.length > 300) SurfaceError.copy(alpha = 0.1f)
                                        else SurfaceBrand.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "${comment.length}/500",
                                            fontSize = FontSize.SMALL,
                                            fontFamily = RobotoCondensedFont(),
                                            color = if (comment.length > 300) SurfaceError else TextSecondary,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = comment,
                                    onValueChange = { if (it.length <= 500) comment = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    placeholder = {
                                        Text(
                                            text = "What did you love about this product? Share details that would help other customers make their decision...",
                                            color = TextSecondary.copy(alpha = 0.7f),
                                            fontFamily = RobotoCondensedFont(),
                                            fontSize = FontSize.SMALL
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SurfaceBrand,
                                        unfocusedBorderColor = BorderIdle,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        cursorColor = SurfaceBrand,
                                        focusedContainerColor = SurfaceBrand.copy(alpha = 0.05f),
                                        unfocusedContainerColor = SurfaceLighter
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    maxLines = 6
                                )

                                if (comment.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Surface(
                                        color = SurfaceBrand.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "💡 Tip: Mention specific features, quality, or how it met your expectations",
                                            fontSize = FontSize.EXTRA_SMALL,
                                            fontFamily = RobotoCondensedFont(),
                                            color = TextSecondary,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        EnhancedSubmitButton(
                            isEnabled = rating > 0 && comment.isNotBlank() && !isSubmitting,
                            isSubmitting = isSubmitting,
                            onClick = {
                                isSubmitting = true
                                productDetailViewModel.addReview(
                                    rating = rating,
                                    comment = comment,
                                    onSuccess = {
                                        isSubmitting = false
                                        messageBarState.addSuccess("🎉 Review submitted successfully!")
                                        navigateBack()
                                    },
                                    onError = { message ->
                                        isSubmitting = false
                                        messageBarState.addError(message)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedRatingStars(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    maxRating: Int = 5,
    size: androidx.compose.ui.unit.Dp = 36.dp,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        repeat(maxRating) { index ->
            val starIndex = index + 1
            val isSelected = starIndex <= rating

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            Box(
                modifier = Modifier
                    .scale(scale)
                    .clip(CircleShape)
                    .clickable { onRatingChange(starIndex.toFloat()) }
                    .background(
                        if (isSelected) SurfaceBrand.copy(alpha = 0.2f) else Color.Transparent,
                        CircleShape
                    )
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Star $starIndex",
                    tint = if (isSelected) Color(0xFFFFD700) else BorderIdle,
                    modifier = Modifier.size(size)
                )
            }
        }
    }
}

@Composable
private fun AnimatedRatingText(rating: Float) {
    val ratingText = getRatingText(rating)
    val textColor by animateColorAsState(
        targetValue = when (rating.toInt()) {
            1, 2 -> SurfaceError
            3 -> TextSecondary
            4, 5 -> Color(0xFF4CAF50)
            else -> TextSecondary
        },
        animationSpec = tween(durationMillis = 300)
    )

    Surface(
        color = textColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = ratingText,
            fontSize = FontSize.MEDIUM,
            fontWeight = FontWeight.Medium,
            fontFamily = RobotoCondensedFont(),
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun EnhancedSubmitButton(
    isEnabled: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText = when {
        isSubmitting -> "✨ Submitting Review..."
        isEnabled -> "🚀 Submit Review"
        else -> "📝 Complete Your Review"
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isEnabled) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = modifier.scale(animatedScale)
    ) {
        if (isSubmitting) {
            Button(
                onClick = { },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceBrand.copy(alpha = 0.7f),
                    disabledContainerColor = SurfaceBrand.copy(alpha = 0.7f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = buttonText,
                        fontSize = FontSize.MEDIUM,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = RobotoCondensedFont(),
                        color = TextPrimary
                    )
                }
            }
        } else {
            Button(
                onClick = onClick,
                enabled = isEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = if (isEnabled) 8.dp else 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = if (isEnabled) SurfaceBrand.copy(alpha = 0.3f) else Color.Transparent
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEnabled) SurfaceBrand else ButtonDisabled,
                    contentColor = TextPrimary,
                    disabledContainerColor = ButtonDisabled,
                    disabledContentColor = TextPrimary.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = RobotoCondensedFont()
                )
            }
        }
    }
}
private fun getRatingText(rating: Float): String {
    return when (rating.toInt()) {
        1 -> "😞 Poor"
        2 -> "😐 Fair"
        3 -> "😊 Good"
        4 -> "😍 Very Good"
        5 -> "🤩 Excellent"
        else -> "👆 Tap a star to rate"
    }
} 