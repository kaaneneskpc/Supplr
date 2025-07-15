package com.kaaneneskpc.supplr.admin_panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.component.InfoCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.component.ProductCard
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.util.DisplayResult
import kotlinx.coroutines.sync.Mutex
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminPanelScreen(
    navigateBack: () -> Unit,
    navigateToManageProduct: (String?) -> Unit
) {
    val adminViewModel = koinViewModel<AdminPanelViewModel>()
    val products = adminViewModel.products.collectAsState()

    LaunchedEffect(products.value) {

    }
    CommonScaffold(
        title = "Admin Panel",
        navigateBack = navigateBack,
        actions = {
            IconButton(onClick = { /* TODO: Add action */ }) {
                Icon(
                    painter = painterResource(Resources.Icon.Search),
                    contentDescription = "Search Icon",
                    tint = IconPrimary
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToManageProduct(null) },
                containerColor = ButtonPrimary,
                contentColor = TextPrimary,
                content = {
                    Icon(
                        painter = painterResource(Resources.Icon.Plus),
                        contentDescription = "Add Icon"
                    )
                }
            )
        }
    ) { paddingValues ->
        products.value.DisplayResult(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ),
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { productList ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = productList,
                        key = { it.id }
                    ) {product ->
                        ProductCard(
                            product = product,
                            onClick = {navigateToManageProduct(product.id)}
                        )
                    }
                }

            },
            onError = { errorMessage ->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Oops!",
                    subtitle = errorMessage
                )},
        )

    }
}
