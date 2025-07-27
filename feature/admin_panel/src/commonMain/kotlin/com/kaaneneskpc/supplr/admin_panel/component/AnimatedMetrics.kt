package com.kaaneneskpc.supplr.admin_panel.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlue
import com.kaaneneskpc.supplr.shared.fonts.CategoryGreen
import com.kaaneneskpc.supplr.shared.fonts.CategoryRed
import com.kaaneneskpc.supplr.shared.fonts.Resources
import kotlinx.coroutines.delay

@Composable
fun AnimatedMetricCardsRow(
    totalRevenue: Double,
    totalOrders: Int,
    averageOrderValue: Double,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(600)
            ) + slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialOffsetY = { it / 2 }
            ),
            modifier = Modifier.weight(1f)
        ) {
            EnhancedMetricCard(
                title = "Total Revenue",
                value = "$${totalRevenue.toInt()}",
                icon = Resources.Icon.Dollar,
                color = CategoryGreen,
                subtitle = "This period"
            )
        }
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(600, delayMillis = 100)
            ) + slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialOffsetY = { it / 2 }
            ),
            modifier = Modifier.weight(1f)
        ) {
            EnhancedMetricCard(
                title = "Total Orders",
                value = totalOrders.toString(),
                icon = Resources.Icon.Book,
                color = CategoryBlue,
                subtitle = "Orders placed"
            )
        }
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(600, delayMillis = 200)
            ) + slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialOffsetY = { it / 2 }
            ),
            modifier = Modifier.weight(1f)
        ) {
            EnhancedMetricCard(
                title = "Avg Order",
                value = "$${averageOrderValue.toInt()}",
                icon = Resources.Icon.Menu,
                color = CategoryRed,
                subtitle = "Per order"
            )
        }
    }
}

@Composable
fun AnimatedChartContainer(
    visible: Boolean = true,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible) {
            delay(delayMillis.toLong())
            isVisible = true
        }
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(800, delayMillis = delayMillis)
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialOffsetY = { it / 3 }
        )
    ) {
        content()
    }
} 