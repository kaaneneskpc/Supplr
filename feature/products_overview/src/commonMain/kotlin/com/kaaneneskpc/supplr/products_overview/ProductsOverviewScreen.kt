package com.kaaneneskpc.supplr.products_overview

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.InfoCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.component.ProductCard
import com.kaaneneskpc.supplr.shared.fonts.Alpha
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.util.DisplayResult
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductsOverviewScreen() {
    val productsOverviewViewModel = koinViewModel<ProductsOverviewViewModel>()
    val products = productsOverviewViewModel.products.collectAsState()

    products.value.DisplayResult(
        onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
        onSuccess = { productList ->
            AnimatedContent(
                targetState = productList
            ) { products ->
                if (products.isNotEmpty()) {
                    Column {
                        Text(
                            modifier = Modifier.fillMaxWidth().alpha(Alpha.HALF),
                            text = "Discounted Products",
                            fontSize = FontSize.EXTRA_REGULAR,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(
                                items = products,
                                key = { it.id }
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        // Handle product click
                                    }
                                )
                            }
                        }
                    }
                } else {
                    InfoCard(
                        image = Resources.Image.Cat,
                        title = "No Products",
                        subtitle = "There are no products available at the moment."
                    )
                }
            }
        },
        onError = {
            InfoCard(
                image = Resources.Image.Cat,
                title = "Oops!",
                subtitle = "Products not found."
            )
        }
    )
}