package com.kaaneneskpc.supplr.di

import com.kaaneneskpc.supplr.admin_panel.AdminPanelViewModel
import com.kaaneneskpc.supplr.auth.AuthViewModel
import com.kaaneneskpc.supplr.data.AdminRepositoryImpl
import com.kaaneneskpc.supplr.data.CustomerRepositoryImpl
import com.kaaneneskpc.supplr.data.ProductRepositoryImpl
import com.kaaneneskpc.supplr.data.domain.AdminRepository
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.data.domain.ProductRepository
import com.kaaneneskpc.supplr.home.HomeViewModel
import com.kaaneneskpc.supplr.manage_product.ManageProductViewModel
import com.kaaneneskpc.supplr.products_overview.ProductsOverviewViewModel
import com.kaaneneskpc.supplr.profile.ProfileViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl() }
    single<AdminRepository> { AdminRepositoryImpl() }
    single<ProductRepository> { ProductRepositoryImpl() }
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ManageProductViewModel)
    viewModelOf(::AdminPanelViewModel)
    viewModelOf(::ProductsOverviewViewModel)
}

expect val targetModule: Module

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null,
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, targetModule)
    }
}