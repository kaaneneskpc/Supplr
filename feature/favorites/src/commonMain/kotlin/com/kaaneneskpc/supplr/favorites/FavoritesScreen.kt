package com.kaaneneskpc.supplr.favorites

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    navigateToDetails: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val favoritesViewModel = koinViewModel<FavoritesViewModel>()
    val screenState = favoritesViewModel.screenState
}