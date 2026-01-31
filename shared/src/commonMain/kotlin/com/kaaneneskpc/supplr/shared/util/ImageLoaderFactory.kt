package com.kaaneneskpc.supplr.shared.util

import coil3.ImageLoader
import coil3.PlatformContext

expect fun createImageLoader(context: PlatformContext): ImageLoader

object ImageLoaderConfig {
    const val MEMORY_CACHE_PERCENT = 0.25
    const val DISK_CACHE_SIZE_BYTES = 100L * 1024 * 1024
    const val CROSSFADE_DURATION_MILLIS = 300
}
