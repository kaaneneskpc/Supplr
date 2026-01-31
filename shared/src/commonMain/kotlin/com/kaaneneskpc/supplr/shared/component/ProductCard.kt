package com.kaaneneskpc.supplr.shared.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import androidx.compose.ui.zIndex

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: (String) -> Unit,
    favoriteIcon: (@Composable () -> Unit)? = null
) {
    var pressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(targetValue = if (pressed) 1.03f else 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        tryAwaitRelease()
                    },
                    onTap = { onClick(product.id) }
                )
            }
            .scale(animatedScale)
    ) {
        CachedAsyncImage(
            imageUrl = product.thumbnail,
            contentDescription = "Product Thumbnail Image",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            shape = RoundedCornerShape(24.dp)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )
        if (favoriteIcon != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .zIndex(2f)
            ) {
                favoriteIcon()
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (product.isDiscounted) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE53935).copy(alpha = 0.92f)
                ) {
                    Text(
                        text = "Discounted",
                        color = Color.White,
                        fontSize = FontSize.EXTRA_SMALL,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.56f))
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                text = product.title,
                fontSize = FontSize.EXTRA_MEDIUM,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = product.description,
                fontSize = FontSize.REGULAR,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${product.price}",
                    fontSize = FontSize.REGULAR,
                    color = Color(0xFFFFEB3B),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}