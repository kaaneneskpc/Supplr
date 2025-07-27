package com.kaaneneskpc.supplr.admin_panel.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlue
import com.kaaneneskpc.supplr.shared.fonts.CategoryGreen
import com.kaaneneskpc.supplr.shared.fonts.CategoryRed
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun EnhancedMetricCard(
    title: String,
    value: String,
    icon: DrawableResource,
    color: Color = ButtonPrimary,
    subtitle: String? = null,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with background circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Value
            Text(
                text = value,
                fontFamily = BebasNeueFont(),
                fontSize = FontSize.LARGE,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            
            // Subtitle (optional)
            subtitle?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MetricCardsRow(
    totalRevenue: Double,
    totalOrders: Int,
    averageOrderValue: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EnhancedMetricCard(
            title = "Total Revenue",
            value = "$${totalRevenue.toInt()}",
            icon = Resources.Icon.Dollar, // Finance icon'ını kullanıyoruz
            color = CategoryGreen,
            subtitle = "This period",
            modifier = Modifier.weight(1f)
        )
        
        EnhancedMetricCard(
            title = "Total Orders",
            value = totalOrders.toString(),
            icon = Resources.Icon.Book, // Book icon'ını kullanıyoruz
            color = CategoryBlue,
            subtitle = "Orders placed",
            modifier = Modifier.weight(1f)
        )
        
        EnhancedMetricCard(
            title = "Avg Order",
            value = "$${averageOrderValue.toInt()}",
            icon = Resources.Icon.Checkmark, // Order icon'ını kullanıyoruz
            color = CategoryRed,
            subtitle = "Per order",
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Compact version for smaller spaces
 */
@Composable
fun CompactMetricCard(
    title: String,
    value: String,
    changePercent: String? = null,
    isPositive: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value,
                    fontFamily = BebasNeueFont(),
                    fontSize = FontSize.MEDIUM,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                changePercent?.let { percent ->
                    Text(
                        text = percent,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPositive) CategoryGreen else CategoryRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
} 