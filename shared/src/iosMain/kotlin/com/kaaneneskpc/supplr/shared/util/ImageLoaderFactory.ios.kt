package com.kaaneneskpc.supplr.shared.util

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun createImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .crossfade(ImageLoaderConfig.CROSSFADE_DURATION_MILLIS)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, ImageLoaderConfig.MEMORY_CACHE_PERCENT)
                .build()
        }
        .diskCache {
            val cacheDir = NSSearchPathForDirectoriesInDomains(
                NSCachesDirectory,
                NSUserDomainMask,
                true
            ).first() as String
            DiskCache.Builder()
                .directory("$cacheDir/image_cache".toPath())
                .maxSizeBytes(ImageLoaderConfig.DISK_CACHE_SIZE_BYTES)
                .build()
        }
        .build()
}
