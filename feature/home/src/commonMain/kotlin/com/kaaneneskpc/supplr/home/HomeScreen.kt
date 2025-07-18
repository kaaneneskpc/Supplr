package com.kaaneneskpc.supplr.home

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaaneneskpc.supplr.cart.CartScreen
import com.kaaneneskpc.supplr.categories.CategoriesScreen
import com.kaaneneskpc.supplr.home.component.BottomBar
import com.kaaneneskpc.supplr.home.component.CustomDrawer
import com.kaaneneskpc.supplr.home.domain.BottomBarDestination
import com.kaaneneskpc.supplr.home.domain.CustomDrawerState
import com.kaaneneskpc.supplr.home.domain.isOpened
import com.kaaneneskpc.supplr.home.domain.opposite
import com.kaaneneskpc.supplr.products_overview.ProductsOverviewScreen
import com.kaaneneskpc.supplr.shared.fonts.Alpha
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextWhite
import com.kaaneneskpc.supplr.shared.navigation.Screen
import com.kaaneneskpc.supplr.shared.util.RequestState
import com.kaaneneskpc.supplr.shared.util.getScreenWidth
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAuth: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToAdminPanel: () -> Unit,
    navigateToDetails: (String) -> Unit,
    navigateToCategorySearch: (String) -> Unit,
    navigateToCheckout: (String) -> Unit
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState()
    val selectedDestination by remember {
        derivedStateOf {
            val route = currentRoute.value?.destination?.route.toString()
            when {
                route.contains(BottomBarDestination.Products.screen.toString()) -> BottomBarDestination.Products
                route.contains(BottomBarDestination.Cart.screen.toString()) -> BottomBarDestination.Cart
                route.contains(BottomBarDestination.Categories.screen.toString()) -> BottomBarDestination.Categories
                else -> BottomBarDestination.Products
            }
        }
    }

    val homeViewModel = koinViewModel<HomeViewModel>()
    val customer by homeViewModel.customer.collectAsState()
    val screenWidth = remember { getScreenWidth() }
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }
    val totalAmount by homeViewModel.totalAmountFlow.collectAsState(RequestState.Loading)

    LaunchedEffect(totalAmount) {
        println("TOTAL AMOUNT: $totalAmount")
    }

    val offsetValue by remember { derivedStateOf { (screenWidth / 1.5).dp } }
    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
    )

    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) SurfaceLighter else Surface
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )

    val animatedRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )

    val messageBarState = rememberMessageBarState()

    Box(modifier = Modifier.fillMaxSize().background(animatedBackground).systemBarsPadding()) {
        CustomDrawer(
            customer = customer,
            onProfileClick = navigateToProfile,
            onContactUsClick = { /* TODO: Handle contact us click */ },
            onSignOutClick = {
                homeViewModel.signOut(
                    onSuccess = navigateToAuth,
                    onError = { errorMessage ->
                        messageBarState.addError(errorMessage)
                    }
                )
            },
            onAdminPanelClick = navigateToAdminPanel
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size = animatedRadius))
                .offset(x = animatedOffset)
                .scale(scale = animatedScale)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(size = animatedRadius),
                    ambientColor = Color.Black.copy(alpha = Alpha.DISABLED),
                    spotColor = Color.Black.copy(alpha = Alpha.DISABLED)
                )
        ) {
            Scaffold(
                containerColor = Surface,
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            AnimatedContent(
                                targetState = selectedDestination
                            ) {
                                Text(
                                    text = selectedDestination.title,
                                    fontFamily = BebasNeueFont(),
                                    fontSize = FontSize.LARGE,
                                    color = TextPrimary
                                )
                            }
                        },
                        actions = {
                            AnimatedVisibility(
                                visible = selectedDestination == BottomBarDestination.Cart
                            ) {
                                if (customer.isSuccess() && customer.getSuccessData().cart.isNotEmpty()) {
                                    IconButton(onClick = {
                                        if (totalAmount.isSuccess()) {
                                            navigateToCheckout(
                                                totalAmount.getSuccessData().toString()
                                            )
                                        } else if (totalAmount.isError()) {
                                            messageBarState.addError(
                                                "Error while fetching total amount: ${totalAmount.getErrorMessage()}"
                                            )
                                        }
                                    }) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.RightArrow),
                                            contentDescription = "Right Icon",
                                            tint = IconPrimary
                                        )
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            AnimatedContent(
                                targetState = drawerState
                            ) { drawer ->
                                if (drawer.isOpened()) {
                                    IconButton(onClick = { drawerState = drawerState.opposite() }) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Close),
                                            contentDescription = "Close Icon",
                                            tint = IconPrimary
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { drawerState = drawerState.opposite() }) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Menu),
                                            contentDescription = "Menu Icon",
                                            tint = IconPrimary
                                        )
                                    }
                                }
                            }
                        }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Surface,
                            scrolledContainerColor = Surface,
                            navigationIconContentColor = IconPrimary,
                            titleContentColor = TextPrimary,
                            actionIconContentColor = IconPrimary
                        )
                    )
                }
            ) { paddingValues ->
                ContentWithMessageBar(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                    messageBarState = messageBarState,
                    errorMaxLines = 2,
                    contentBackgroundColor = Surface
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Products,
                            modifier = Modifier.weight(1f)
                        ) {
                            composable<Screen.Products> {
                                ProductsOverviewScreen(
                                    navigateToDetails = navigateToDetails
                                )
                            }
                            composable<Screen.Cart> {
                                CartScreen()
                            }
                            composable<Screen.Categories> {
                                CategoriesScreen(
                                    navigateToCategorySearch = navigateToCategorySearch
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier.padding(all = 12.dp)
                        ) {
                            BottomBar(
                                customer = customer,
                                selected = selectedDestination,
                                onSelect = { destination ->
                                    navController.navigate(destination.screen) {
                                        launchSingleTop = true
                                        popUpTo<Screen.Products> {
                                            saveState = true
                                            inclusive = false
                                        }
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}