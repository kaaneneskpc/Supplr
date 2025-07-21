package com.kaaneneskpc.supplr.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.InfoCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.component.ProductCard
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.util.DisplayResult
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    navigateToDetails: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val favoritesViewModel = koinViewModel<FavoritesViewModel>()
    val favoriteProductsState by favoritesViewModel.favoriteProducts.collectAsState()

    CommonScaffold(
        title = "Favorites",
        navigateBack = navigateBack
    ) { paddingValues ->
        favoriteProductsState.DisplayResult(
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { products ->
                if (products.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(products, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { navigateToDetails(product.id) },
                                modifier = Modifier.fillMaxWidth(),
                                favoriteIcon = {
                                    IconButton(
                                        onClick = {
                                            favoritesViewModel.removeFromFavorites(
                                                product.id,
                                                onSuccess = {},
                                                onError = {}
                                            )
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Favorites),
                                            contentDescription = "Remove from favorites",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            )
                        }
                    }
                } else {
                    InfoCard(
                        image = Resources.Image.Cat,
                        title = "No Favorites",
                        subtitle = "You haven't added any favorites yet."
                    )
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