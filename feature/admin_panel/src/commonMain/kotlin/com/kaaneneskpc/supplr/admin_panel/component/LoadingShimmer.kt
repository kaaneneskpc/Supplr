package com.kaaneneskpc.supplr.admin_panel.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary

@Composable
fun LoadingShimmer(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        TextPrimary.copy(alpha = 0.1f),
        TextPrimary.copy(alpha = 0.3f),
        TextPrimary.copy(alpha = 0.1f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
    )
}

@Composable
fun MetricCardShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LoadingShimmer(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
            )

            LoadingShimmer(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
            )

            LoadingShimmer(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(24.dp)
            )

            LoadingShimmer(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
            )
        }
    }
}

@Composable
fun ChartShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            LoadingShimmer(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LoadingShimmer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

@Composable
fun ProductListShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LoadingShimmer(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                )
                
                LoadingShimmer(
                    modifier = Modifier
                        .width(60.dp)
                        .height(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product items shimmer
            repeat(5) {
                ProductItemShimmer()
                if (it < 4) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ProductItemShimmer() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Rank badge shimmer
        LoadingShimmer(
            modifier = Modifier.size(32.dp)
        )
        
        // Product icon shimmer
        LoadingShimmer(
            modifier = Modifier.size(40.dp)
        )
        
        // Product details shimmer
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LoadingShimmer(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
            )
            
            LoadingShimmer(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
            )
        }
        
        // Performance indicator shimmer
        LoadingShimmer(
            modifier = Modifier
                .width(50.dp)
                .height(24.dp)
        )
    }
}

@Composable
fun DashboardLoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Metric cards shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                MetricCardShimmer(
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Charts shimmer
        ChartShimmer(
            modifier = Modifier.fillMaxWidth()
        )
        
        ChartShimmer(
            modifier = Modifier.fillMaxWidth()
        )
        
        // Products shimmer
        ProductListShimmer(
            modifier = Modifier.fillMaxWidth()
        )
    }
} 