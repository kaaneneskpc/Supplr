package com.kaaneneskpc.supplr

import ContentWithMessageBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Rate this product",
                            fontSize = FontSize.LARGE,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = RobotoCondensedFont(),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InteractiveRatingStars(
                            rating = rating,
                            onRatingChange = { rating = it },
                            size = 40.dp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = getRatingText(rating),
                            fontSize = FontSize.MEDIUM,
                            fontFamily = RobotoCondensedFont(),
                            color = TextSecondary
                        )
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Share your thoughts",
                            fontSize = FontSize.LARGE,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = RobotoCondensedFont(),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = {
                                Text(
                                    text = "Tell others about your experience with this product...",
                                    color = TextSecondary,
                                    fontFamily = RobotoCondensedFont()
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SurfaceBrand,
                                unfocusedBorderColor = BorderIdle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = SurfaceBrand
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 5
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                SupplrButton(
                    text = if (isSubmitting) "Submitting..." else "Submit Review",
                    enabled = rating > 0 && comment.isNotBlank() && !isSubmitting,
                    onClick = {
                        isSubmitting = true
                        productDetailViewModel.addReview(
                            rating = rating,
                            comment = comment,
                            onSuccess = {
                                isSubmitting = false
                                messageBarState.addSuccess("Review submitted successfully!")
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

@Composable
private fun InteractiveRatingStars(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    maxRating: Int = 5,
    size: androidx.compose.ui.unit.Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        repeat(maxRating) { index ->
            val starIndex = index + 1
            val isSelected = starIndex <= rating

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star $starIndex",
                tint = if (isSelected) androidx.compose.ui.graphics.Color(0xFFFFD700)
                else androidx.compose.ui.graphics.Color(0xFFE0E0E0),
                modifier = Modifier
                    .size(size)
                    .clickable { onRatingChange(starIndex.toFloat()) }
            )
        }
    }
}

private fun getRatingText(rating: Float): String {
    return when (rating.toInt()) {
        1 -> "Poor"
        2 -> "Fair"
        3 -> "Good"
        4 -> "Very Good"
        5 -> "Excellent"
        else -> "Tap a star to rate"
    }
} 