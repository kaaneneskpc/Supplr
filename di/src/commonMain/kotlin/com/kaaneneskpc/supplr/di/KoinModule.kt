package com.kaaneneskpc.supplr.di


import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.kaaneneskpc.supplr.auth.AuthViewModel
import com.kaaneneskpc.supplr.customer.CustomerRepository
import com.kaaneneskpc.supplr.customer.CustomerRepositoryImpl

val sharedModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl() }
    viewModelOf(::AuthViewModel)
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule)
    }
}