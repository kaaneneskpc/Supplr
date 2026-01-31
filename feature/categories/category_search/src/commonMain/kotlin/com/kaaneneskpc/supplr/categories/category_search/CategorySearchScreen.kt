package com.kaaneneskpc.supplr.categories.category_search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.InfoCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.component.ProductCard
import com.kaaneneskpc.supplr.shared.domain.PaginationState
import com.kaaneneskpc.supplr.shared.domain.ProductCategory
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.BorderIdle
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextSecondary
import com.kaaneneskpc.supplr.shared.util.DisplayResult
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySearchScreen(
    category: ProductCategory,
    navigateToDetails: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel = koinViewModel<CategorySearchViewModel>()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val paginationState by viewModel.paginationState.collectAsState()
    var searchBarVisible by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= totalItems - 3 && totalItems > 0
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .collect { shouldLoad ->
                if (shouldLoad && paginationState == PaginationState.Idle) {
                    viewModel.loadNextPage()
                }
            }
    }
    Scaffold(
        containerColor = Surface,
        topBar = {
            AnimatedContent(
                targetState = searchBarVisible
            ) { visible ->
                if (visible) {
                    SearchBar(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(),
                        inputField = {
                            SearchBarDefaults.InputField(
                                modifier = Modifier.fillMaxWidth(),
                                query = searchQuery,
                                onQueryChange = viewModel::updateSearchQuery,
                                expanded = false,
                                onExpandedChange = {},
                                onSearch = {},
                                placeholder = {
                                    Text(
                                        text = "Search here",
                                        fontSize = FontSize.REGULAR,
                                        color = TextPrimary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        modifier = Modifier.size(14.dp),
                                        onClick = {
                                            if (searchQuery.isNotEmpty()) viewModel.updateSearchQuery("")
                                            else searchBarVisible = false
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Close),
                                            contentDescription = "Close icon"
                                        )
                                    }
                                }
                            )
                        },
                        colors = SearchBarColors(
                            containerColor = SurfaceLighter,
                            dividerColor = BorderIdle
                        ),
                        expanded = false,
                        onExpandedChange = {},
                        content = {}
                    )
                } else {
                    TopAppBar(
                        title = {
                            Text(
                                text = category.title,
                                fontFamily = BebasNeueFont(),
                                fontSize = FontSize.LARGE,
                                color = TextPrimary
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = navigateBack) {
                                Icon(
                                    painter = painterResource(Resources.Icon.BackArrow),
                                    contentDescription = "Back Arrow icon",
                                    tint = IconPrimary
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { searchBarVisible = true }) {
                                Icon(
                                    painter = painterResource(Resources.Icon.Search),
                                    contentDescription = "Search icon",
                                    tint = IconPrimary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Surface,
                            scrolledContainerColor = Surface,
                            navigationIconContentColor = IconPrimary,
                            titleContentColor = TextPrimary,
                            actionIconContentColor = IconPrimary
                        )
                    )
                }
            }
        }
    ) { padding ->
        filteredProducts.DisplayResult(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { categoryProducts ->
                AnimatedContent(
                    targetState = categoryProducts
                ) { products ->
                    if (products.isNotEmpty()) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = products,
                                key = { it.id }
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = navigateToDetails
                                )
                            }
                            item {
                                PaginationFooter(paginationState = paginationState)
                            }
                        }
                    } else {
                        InfoCard(
                            image = Resources.Image.Cat,
                            title = "Nothing here",
                            subtitle = "We couldn't find any product."
                        )
                    }
                }
            },
            onError = { message ->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Oops!",
                    subtitle = message
                )
            },
            transitionSpec = fadeIn() togetherWith fadeOut()
        )
    }
}

@Composable
private fun PaginationFooter(
    paginationState: PaginationState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (paginationState) {
            is PaginationState.LoadingMore -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = SurfaceBrand,
                    strokeWidth = 2.dp
                )
            }
            is PaginationState.EndReached -> {
                Text(
                    text = "End of list",
                    fontSize = FontSize.REGULAR,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            is PaginationState.Error -> {
                Text(
                    text = "Error loading more items",
                    fontSize = FontSize.REGULAR,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            else -> {}
        }
    }
}