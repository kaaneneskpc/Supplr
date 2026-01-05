package com.kaaneneskpc.supplr

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kaaneneskpc.supplr.component.FlavorChip
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.component.InfoCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.component.QuantityCounter
import com.kaaneneskpc.supplr.shared.component.SupplrButton
import com.kaaneneskpc.supplr.shared.domain.ProductCategory
import com.kaaneneskpc.supplr.shared.domain.QuantityCounterSize
import com.kaaneneskpc.supplr.component.ReviewsSection
import com.kaaneneskpc.supplr.shared.fonts.BorderIdle
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.RobotoCondensedFont
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextSecondary
import com.kaaneneskpc.supplr.shared.fonts.TextWhite
import com.kaaneneskpc.supplr.shared.util.DisplayResult
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState
import com.kaaneneskpc.supplr.shared.util.RequestState

@Composable
fun ProductDetailsScreen(
    id: String?,
    navigateBack: () -> Unit,
    navigateToReviewScreen: (String?) -> Unit
) {
    val messageBarState = rememberMessageBarState()
    val productDetailViewModel = koinViewModel<ProductDetailViewModel>()
    val product by productDetailViewModel.product.collectAsState()
    val quantity = productDetailViewModel.quantity
    val selectedFlavor = productDetailViewModel.selectedFlavor
    val favoriteProductIdsState by productDetailViewModel.favoriteProductIds.collectAsState()
    val reviewsState by productDetailViewModel.reviews.collectAsState()
    val averageRating by productDetailViewModel.averageRating.collectAsState()
    val reviewCount by productDetailViewModel.reviewCount.collectAsState()
    val hasUserReviewed = productDetailViewModel.hasUserReviewed
    val userVotes by productDetailViewModel.userVotes.collectAsState()


    CommonScaffold(
        title = "Product Details",
        navigateBack = navigateBack,
        actions = {
            QuantityCounter(
                size = QuantityCounterSize.Large,
                value = quantity,
                onMinusClick = productDetailViewModel::updateQuantity,
                onPlusClick = productDetailViewModel::updateQuantity
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    ) { paddingValues ->
        product.DisplayResult(
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { selectedProduct ->
                ContentWithMessageBar(
                    contentBackgroundColor = Surface,
                    modifier = Modifier
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                    messageBarState = messageBarState,
                    errorMaxLines = 2,
                    errorContainerColor = SurfaceError,
                    errorContentColor = TextWhite,
                    successContainerColor = SurfaceBrand,
                    successContentColor = TextPrimary
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                            .padding(top = 12.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(size = 12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = BorderIdle,
                                        shape = RoundedCornerShape(size = 12.dp)
                                    ),
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(selectedProduct.thumbnail)
                                    .crossfade(enable = true)
                                    .build(),
                                contentDescription = "Product thumbnail image",
                                contentScale = ContentScale.Crop
                            )
                            val favoriteIds =
                                if (favoriteProductIdsState is RequestState.Success) (favoriteProductIdsState as RequestState.Success<List<String>>).data else emptyList()
                            IconButton(
                                onClick = {
                                    if (selectedProduct.id in favoriteIds) {
                                        messageBarState.addError("This product is already in your favorites.")
                                    } else {
                                        productDetailViewModel.addToFavorites(
                                            onSuccess = { messageBarState.addSuccess("Added to favorites!") },
                                            onError = { message -> messageBarState.addError(message) }
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .zIndex(2f)
                            ) {
                                Icon(
                                    painter = painterResource(Resources.Icon.Favorites),
                                    contentDescription = "Add to favorites",
                                    tint = if (selectedProduct.id in favoriteIds) Color.Red else Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AnimatedContent(
                                targetState = selectedProduct.category
                            ) { category ->
                                if (ProductCategory.valueOf(category) == ProductCategory.SaladMixed) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(14.dp),
                                            painter = painterResource(Resources.Icon.Weight),
                                            contentDescription = "Weight icon"
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${selectedProduct.weight}g",
                                            fontSize = FontSize.REGULAR,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "$${selectedProduct.price}",
                                fontSize = FontSize.MEDIUM,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = selectedProduct.title,
                            fontSize = FontSize.EXTRA_MEDIUM,
                            fontWeight = FontWeight.Medium,
                            fontFamily = RobotoCondensedFont(),
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = selectedProduct.description,
                            fontSize = FontSize.REGULAR,
                            lineHeight = FontSize.REGULAR * 1.3,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(
                            modifier = Modifier
                                .background(
                                    if (selectedProduct.flavors?.isNotEmpty() == true) SurfaceLighter
                                    else Surface
                                )
                                .padding(all = 24.dp)
                        ) {
                            if (selectedProduct.flavors?.isNotEmpty() == true) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    selectedProduct.flavors?.forEach { flavor ->
                                        FlavorChip(
                                            flavor = flavor,
                                            isSelected = selectedFlavor == flavor,
                                            onClick = { productDetailViewModel.updateFlavor(flavor) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                            SupplrButton(
                                icon = Resources.Icon.ShoppingCart,
                                text = "Add to Cart",
                                enabled = if (selectedProduct.flavors?.isNotEmpty() == true) selectedFlavor != null
                                else true,
                                onClick = {
                                    productDetailViewModel.addItemToCart(
                                        onSuccess = { messageBarState.addSuccess("Product added to cart.") },
                                        onError = { message -> messageBarState.addError(message) }
                                    )
                                }
                            )
                        }

                        Column(
                            modifier = Modifier
                                .background(Surface)
                                .padding(all = 4.dp)
                        ) {
                            Text(
                                text = "Reviews",
                                fontSize = FontSize.EXTRA_LARGE,
                                fontWeight = FontWeight.Bold,
                                fontFamily = RobotoCondensedFont(),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            ReviewsSection(
                                reviewsState = reviewsState,
                                averageRating = averageRating,
                                reviewCount = reviewCount,
                                hasUserReviewed = hasUserReviewed,
                                userVotes = userVotes,
                                onWriteReviewClick = {
                                    navigateToReviewScreen(id)
                                },
                                onHelpfulClick = { reviewId ->
                                    productDetailViewModel.voteReview(
                                        reviewId = reviewId,
                                        isHelpful = true,
                                        onSuccess = { messageBarState.addSuccess("Vote recorded!") },
                                        onError = { message -> messageBarState.addError(message) }
                                    )
                                },
                                onUnhelpfulClick = { reviewId ->
                                    productDetailViewModel.voteReview(
                                        reviewId = reviewId,
                                        isHelpful = false,
                                        onSuccess = { messageBarState.addSuccess("Vote recorded!") },
                                        onError = { message -> messageBarState.addError(message) }
                                    )
                                },
                                onLoadUserVote = { reviewId ->
                                    productDetailViewModel.loadUserVoteForReview(reviewId)
                                }
                            )
                        }
                            }
                        }
                    }
                }
            },
            onError = { message ->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Oops!",
                    subtitle = message
                )
            }
        )
    }
}