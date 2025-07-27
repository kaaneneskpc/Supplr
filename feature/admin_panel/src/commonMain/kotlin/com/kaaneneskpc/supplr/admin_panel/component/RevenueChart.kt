package com.kaaneneskpc.supplr.admin_panel.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.data.domain.DailySummary
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlue
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary

/**
 * Simple Revenue Chart with custom Canvas drawing
 */
@Composable
fun RevenueChart(
    dailySummaries: List<DailySummary>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Revenue",
                fontFamily = BebasNeueFont(),
                fontSize = FontSize.LARGE,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (dailySummaries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No revenue data available",
                        color = TextPrimary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                CustomLineChart(
                    dailySummaries = dailySummaries,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

@Composable
private fun CustomLineChart(
    dailySummaries: List<DailySummary>,
    modifier: Modifier = Modifier
) {
    val maxRevenue = remember(dailySummaries) {
        dailySummaries.maxOfOrNull { it.totalRevenue } ?: 0.0
    }
    
    Canvas(modifier = modifier) {
        if (dailySummaries.isNotEmpty() && maxRevenue > 0) {
            drawLineChart(
                data = dailySummaries,
                maxValue = maxRevenue,
                color = ButtonPrimary
            )
        }
    }
}

private fun DrawScope.drawLineChart(
    data: List<DailySummary>,
    maxValue: Double,
    color: Color
) {
    val width = size.width
    val height = size.height
    val padding = 40f
    
    val chartWidth = width - (padding * 2)
    val chartHeight = height - (padding * 2)
    
    if (data.size < 2) return
    
    val path = Path()
    
    data.forEachIndexed { index, summary ->
        val x = padding + (index.toFloat() / (data.size - 1).toFloat()) * chartWidth
        val y = padding + chartHeight - (summary.totalRevenue.toFloat() / maxValue.toFloat()) * chartHeight
        
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        
        // Draw points
        drawCircle(
            color = color,
            radius = 4f,
            center = Offset(x, y)
        )
    }
    
    // Draw line
    drawPath(
        path = path,
        color = color,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
    )
}

/**
 * Simplified Revenue Chart with horizontal bars
 */
@Composable
fun SimpleRevenueChart(
    dailySummaries: List<DailySummary>,
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Revenue Trend",
                    fontFamily = BebasNeueFont(),
                    fontSize = FontSize.LARGE,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                if (dailySummaries.isNotEmpty()) {
                    Text(
                        text = "${dailySummaries.size} days",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (dailySummaries.isEmpty()) {
                EmptyRevenueState()
            } else {
                HorizontalBarChart(
                    data = dailySummaries.take(7), // Son 7 gün
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun HorizontalBarChart(
    data: List<DailySummary>,
    modifier: Modifier = Modifier
) {
    val maxRevenue = remember(data) {
        data.maxOfOrNull { it.totalRevenue } ?: 1.0
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEach { summary ->
            HorizontalBarItem(
                summary = summary,
                maxRevenue = maxRevenue
            )
        }
    }
}

@Composable
private fun HorizontalBarItem(
    summary: DailySummary,
    maxRevenue: Double
) {
    val percentage = if (maxRevenue > 0) (summary.totalRevenue / maxRevenue).toFloat() else 0f
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date
        Text(
            text = summary.date.takeLast(5), // MM-DD format
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary.copy(alpha = 0.7f),
            modifier = Modifier.width(50.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Progress Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ButtonPrimary.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ButtonPrimary)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Value
        Text(
            text = "$${summary.totalRevenue.toInt()}",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun EmptyRevenueState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CategoryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📊",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "No revenue data yet",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "Revenue charts will appear when orders are placed",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
} 