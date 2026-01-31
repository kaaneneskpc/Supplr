package com.kaaneneskpc.supplr.shared.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.util.ImageLoaderConfig
import com.kaaneneskpc.supplr.shared.util.createImageLoader
import org.jetbrains.compose.resources.painterResource

@Composable
fun CachedAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(0.dp)
) {
    val context = LocalPlatformContext.current
    val imageLoader = remember(context) { createImageLoader(context) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.clip(shape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(ImageLoaderConfig.CROSSFADE_DURATION_MILLIS)
                .memoryCacheKey(imageUrl.hashCode().toString())
                .diskCacheKey(imageUrl.hashCode().toString())
                .build(),
            imageLoader = imageLoader,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
            onState = { state ->
                isLoading = state is AsyncImagePainter.State.Loading
                isError = state is AsyncImagePainter.State.Error
            }
        )
        if (isLoading) {
            ShimmerPlaceholder(
                modifier = Modifier.fillMaxSize()
            )
        }
        if (isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLighter),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Resources.Icon.Warning),
                    contentDescription = "Image load error",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation - 200f, translateAnimation - 200f),
        end = Offset(translateAnimation, translateAnimation)
    )
    Box(
        modifier = modifier.background(brush)
    )
}
