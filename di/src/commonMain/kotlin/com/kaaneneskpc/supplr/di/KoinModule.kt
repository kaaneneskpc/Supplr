package com.kaaneneskpc.supplr.di


import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.kaaneneskpc.supplr.auth.AuthViewModel
import com.kaaneneskpc.supplr.data.CustomerRepository
import com.kaaneneskpc.supplr.data.CustomerRepositoryImpl
import com.kaaneneskpc.supplr.home.HomeViewModel
import com.kaaneneskpc.supplr.profile.ProfileViewModel

val sharedModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl() }
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule)
    }
}