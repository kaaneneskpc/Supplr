package com.kaaneneskpc.supplr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kaaneneskpc.supplr.AddReviewScreen
import com.kaaneneskpc.supplr.ProductDetailsScreen
import com.kaaneneskpc.supplr.auth.AuthScreen
import com.kaaneneskpc.supplr.home.HomeScreen
import com.kaaneneskpc.supplr.profile.ProfileScreen
import com.kaaneneskpc.supplr.shared.navigation.Screen
import com.kaaneneskpc.supplr.admin_panel.AdminDashboardScreen
import com.kaaneneskpc.supplr.admin_panel.AdminPanelScreen
import com.kaaneneskpc.supplr.blog.BlogScreen
import com.kaaneneskpc.supplr.blog.ArticleDetailScreen
import com.kaaneneskpc.supplr.blog.ManageBlogScreen
import com.kaaneneskpc.supplr.categories.category_search.CategorySearchScreen
import com.kaaneneskpc.supplr.checkout.CheckoutScreen
import com.kaaneneskpc.supplr.contact_us.ContactUsScreen
import com.kaaneneskpc.supplr.favorites.FavoritesScreen
import com.kaaneneskpc.supplr.locations.LocationsScreen
import com.kaaneneskpc.supplr.locations.AddEditLocationScreen
import com.kaaneneskpc.supplr.manage_product.ManageProductScreen
import com.kaaneneskpc.supplr.payment_completed.PaymentCompletedScreen
import com.kaaneneskpc.supplr.shared.domain.ProductCategory

@Composable
fun NavGraph(startDestination: Screen = Screen.Auth) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                navigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Auth> { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.Home> {
            HomeScreen(
                navigateToAuth = {
                    navController.navigate(Screen.Auth) {
                        popUpTo<Screen.Auth> { inclusive = true }
                    }
                },
                navigateToProfile = {
                    navController.navigate(Screen.Profile)
                },
                navigateToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                },
                navigateToDetails = { productId ->
                    navController.navigate(Screen.ProductDetails(id = productId))
                },
                navigateToCategorySearch = { categoryName ->
                    navController.navigate(Screen.CategorySearch(categoryName))
                },
                navigateToCheckout = { totalAmount ->
                    navController.navigate(Screen.Checkout(totalAmount))
                },
                navigateToContactUs = {
                    navController.navigate(Screen.ContactUs)
                },
                navigateToFavorites = {
                    navController.navigate(Screen.Favorites)
                },
                navigateToLocations = {
                    navController.navigate(Screen.Locations)
                },
                navigateToBlog = {
                    navController.navigate(Screen.Blog)
                }
            )
        }
        composable<Screen.Profile> {
            ProfileScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.AdminPanel> {
            AdminPanelScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToManageProduct = {
                    navController.navigate(Screen.ManageProduct(it))
                },
                navigateToStatistics = {
                    navController.navigate(Screen.AdminDashboard)
                },
                navigateToBlogManagement = {
                    navController.navigate(Screen.ManageBlog())
                }
            )
        }
        composable<Screen.ManageProduct> {
            val id = it.toRoute<Screen.ManageProduct>().id
            ManageProductScreen(
                id = id,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.AdminDashboard> {
            AdminDashboardScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<Screen.ProductDetails> {
            val id = it.toRoute<Screen.ProductDetails>().id
            ProductDetailsScreen(
                id = id,
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToReviewScreen = { productId ->
                    navController.navigate(Screen.Review(productId))
                }
            )
        }

        composable<Screen.CategorySearch> {
            val category = ProductCategory.valueOf(it.toRoute<Screen.CategorySearch>().category)
            CategorySearchScreen(
                category = category,
                navigateToDetails = { id ->
                    navController.navigate(Screen.ProductDetails(id))
                },
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.Checkout> {
            val totalAmount = it.toRoute<Screen.Checkout>().totalAmount
            CheckoutScreen(
                totalAmount = totalAmount.toDoubleOrNull() ?: 0.0,
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToPaymentCompleted = { isSuccess, error ->
                    navController.navigate(Screen.PaymentCompleted(isSuccess, error))
                }
            )
        }

        composable<Screen.PaymentCompleted> {
            PaymentCompletedScreen(
                navigateBack = {
                    navController.navigate(Screen.Home) {
                        launchSingleTop = true
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.ContactUs> {
            ContactUsScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.Favorites> {
            FavoritesScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToDetails = { productId ->
                    navController.navigate(Screen.ProductDetails(id = productId))
                }
            )
        }

        composable<Screen.Locations> {
            LocationsScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToAddLocation = {
                    navController.navigate(Screen.AddEditLocation())
                },
                navigateToEditLocation = { locationId ->
                    navController.navigate(Screen.AddEditLocation(locationId))
                }
            )
        }

        composable<Screen.AddEditLocation> {
            val locationId = it.toRoute<Screen.AddEditLocation>().locationId
            AddEditLocationScreen(
                locationId = locationId,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.Review> {
            val id = it.toRoute<Screen.Review>().id
            AddReviewScreen(
                productId = id.orEmpty(),
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.Blog> {
            BlogScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToArticle = { articleId ->
                    navController.navigate(Screen.ArticleDetail(articleId))
                }
            )
        }

        composable<Screen.ArticleDetail> {
            val articleId = it.toRoute<Screen.ArticleDetail>().articleId
            ArticleDetailScreen(
                articleId = articleId,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.ManageBlog> {
            val articleId = it.toRoute<Screen.ManageBlog>().articleId
            ManageBlogScreen(
                articleId = articleId,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

    }

}