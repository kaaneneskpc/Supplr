package com.kaaneneskpc.supplr.di

import com.kaaneneskpc.supplr.manage_product.PhotoPicker
import org.koin.dsl.module

actual val targetModule = module {
    single<PhotoPicker> { PhotoPicker() }
}