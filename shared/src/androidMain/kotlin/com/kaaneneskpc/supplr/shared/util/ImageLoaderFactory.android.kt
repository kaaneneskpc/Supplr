package com.kaaneneskpc.supplr.shared.util

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import okio.Path.Companion.toOkioPath
import java.io.File

actual fun createImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .crossfade(ImageLoaderConfig.CROSSFADE_DURATION_MILLIS)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, ImageLoaderConfig.MEMORY_CACHE_PERCENT)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(File(context.cacheDir, "image_cache").toOkioPath())
                .maxSizeBytes(ImageLoaderConfig.DISK_CACHE_SIZE_BYTES)
                .build()
        }
        .build()
}
