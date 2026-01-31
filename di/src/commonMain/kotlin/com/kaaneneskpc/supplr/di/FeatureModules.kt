package com.kaaneneskpc.supplr.di

import com.kaaneneskpc.supplr.ProductDetailViewModel
import com.kaaneneskpc.supplr.admin_panel.AdminCouponsViewModel
import com.kaaneneskpc.supplr.admin_panel.AdminDashboardViewModel
import com.kaaneneskpc.supplr.admin_panel.AdminOrdersViewModel
import com.kaaneneskpc.supplr.admin_panel.AdminPanelViewModel
import com.kaaneneskpc.supplr.auth.AuthViewModel
import com.kaaneneskpc.supplr.cart.CartViewModel
import com.kaaneneskpc.supplr.categories.category_search.CategorySearchViewModel
import com.kaaneneskpc.supplr.checkout.CheckoutViewModel
import com.kaaneneskpc.supplr.data.AdminRepositoryImpl
import com.kaaneneskpc.supplr.data.BlogRepositoryImpl
import com.kaaneneskpc.supplr.data.CouponRepositoryImpl
import com.kaaneneskpc.supplr.data.CustomerRepositoryImpl
import com.kaaneneskpc.supplr.data.FavoritesRepositoryImpl
import com.kaaneneskpc.supplr.data.OrderRepositoryImpl
import com.kaaneneskpc.supplr.data.ProductRepositoryImpl
import com.kaaneneskpc.supplr.data.ReviewRepositoryImpl
import com.kaaneneskpc.supplr.data.LocationRepositoryImpl
import com.kaaneneskpc.supplr.data.PaymentRepositoryImpl
import com.kaaneneskpc.supplr.data.domain.AdminRepository
import com.kaaneneskpc.supplr.data.domain.BlogRepository
import com.kaaneneskpc.supplr.data.domain.CouponRepository
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.data.domain.FavoritesRepository
import com.kaaneneskpc.supplr.data.domain.LocationRepository
import com.kaaneneskpc.supplr.data.domain.OrderRepository
import com.kaaneneskpc.supplr.data.domain.PaymentRepository
import com.kaaneneskpc.supplr.data.domain.ProductRepository
import com.kaaneneskpc.supplr.data.domain.ReviewRepository
import com.kaaneneskpc.supplr.data.usecase.GetDashboardAnalyticsUseCase
import com.kaaneneskpc.supplr.data.usecase.GetUserStatisticsUseCase
import com.kaaneneskpc.supplr.favorites.FavoritesViewModel
import com.kaaneneskpc.supplr.home.HomeViewModel
import com.kaaneneskpc.supplr.locations.LocationsViewModel
import com.kaaneneskpc.supplr.manage_product.ManageProductViewModel
import com.kaaneneskpc.supplr.order_history.OrderHistoryViewModel
import com.kaaneneskpc.supplr.payment_completed.PaymentCompletedViewModel
import com.kaaneneskpc.supplr.products_overview.ProductsOverviewViewModel
import com.kaaneneskpc.supplr.profile.ProfileViewModel
import com.kaaneneskpc.supplr.profile.settings.SettingsViewModel
import com.kaaneneskpc.supplr.profile.settings.ChangePasswordViewModel
import com.kaaneneskpc.supplr.profile.settings.TwoFactorAuthViewModel
import com.kaaneneskpc.supplr.profile.settings.DeleteAccountViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl() }
    single<AdminRepository> { AdminRepositoryImpl() }
    single<ProductRepository> { ProductRepositoryImpl() }
    single<BlogRepository> { BlogRepositoryImpl() }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    single<ReviewRepository> { ReviewRepositoryImpl() }
    single<LocationRepository> { LocationRepositoryImpl() }
    single<PaymentRepository> { PaymentRepositoryImpl() }
    single<CouponRepository> { CouponRepositoryImpl() }
    single { GetDashboardAnalyticsUseCase(get()) }
    single { GetUserStatisticsUseCase(get()) }
}

val authModule = module {
    viewModelOf(::AuthViewModel)
}

val mainModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ProductsOverviewViewModel)
    viewModelOf(::CartViewModel)
}

val adminModule = module {
    viewModelOf(::AdminPanelViewModel)
    viewModelOf(::AdminDashboardViewModel)
    viewModelOf(::AdminOrdersViewModel)
    viewModelOf(::AdminCouponsViewModel)
    viewModelOf(::ManageProductViewModel)
}

val secondaryModule = module {
    viewModelOf(::ProductDetailViewModel)
    viewModelOf(::CategorySearchViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::PaymentCompletedViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::LocationsViewModel)
    viewModelOf(::OrderHistoryViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ChangePasswordViewModel)
    viewModelOf(::TwoFactorAuthViewModel)
    viewModelOf(::DeleteAccountViewModel)
}
