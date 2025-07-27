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
                    val totalRevenue = dailySummaries.sumOf { it.totalRevenue }
                    Text(
                        text = "Total: $${totalRevenue.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (dailySummaries.isEmpty()) {
                EmptyRevenueState()
            } else {
                EnhancedLineChart(
                    dailySummaries = dailySummaries,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                RevenueStatsRow(dailySummaries = dailySummaries)
            }
        }
    }
}

@Composable
private fun EnhancedLineChart(
    dailySummaries: List<DailySummary>,
    modifier: Modifier = Modifier
) {
    val maxRevenue = remember(dailySummaries) {
        dailySummaries.maxOfOrNull { it.totalRevenue } ?: 100.0
    }
    
    Canvas(modifier = modifier) {
        if (dailySummaries.isNotEmpty()) {
            drawEnhancedLineChart(
                data = dailySummaries,
                maxValue = maxRevenue,
                primaryColor = ButtonPrimary,
                secondaryColor = CategoryBlue
            )
        }
    }
}

@Composable
private fun RevenueStatsRow(
    dailySummaries: List<DailySummary>
) {
    if (dailySummaries.isEmpty()) return
    
    val avgRevenue = dailySummaries.map { it.totalRevenue }.average()
    val maxRevenue = dailySummaries.maxOf { it.totalRevenue }
    val minRevenue = dailySummaries.minOf { it.totalRevenue }
    val trend = if (dailySummaries.size >= 2) {
        val recent = dailySummaries.takeLast(3).map { it.totalRevenue }.average()
        val older = dailySummaries.dropLast(3).takeLast(3).map { it.totalRevenue }.average()
        if (recent > older) "📈" else "📉"
    } else "📊"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            label = "Average",
            value = "$${avgRevenue.toInt()}",
            icon = "📊"
        )
        StatItem(
            label = "Peak",
            value = "$${maxRevenue.toInt()}",
            icon = "🔥"
        )
        StatItem(
            label = "Trend",
            value = "${dailySummaries.size} days",
            icon = trend
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary.copy(alpha = 0.7f)
        )
    }
}

private fun DrawScope.drawEnhancedLineChart(
    data: List<DailySummary>,
    maxValue: Double,
    primaryColor: Color,
    secondaryColor: Color
) {
    val width = size.width
    val height = size.height
    val padding = 60f
    
    val chartWidth = width - (padding * 2)
    val chartHeight = height - (padding * 2)
    
    if (data.isEmpty()) {
        drawPlaceholderChart(width, height, padding, primaryColor)
        return
    }

    drawGridLines(width, height, padding, Color.Gray.copy(alpha = 0.3f))

    drawAxes(width, height, padding, Color.Black.copy(alpha = 0.6f))
    
    if (data.size == 1) {
        val x = padding + chartWidth / 2
        val y = padding + chartHeight - (data[0].totalRevenue.toFloat() / maxValue.toFloat()) * chartHeight
        drawCircle(
            color = Color.White,
            radius = 8f,
            center = Offset(x, y)
        )
        drawCircle(
            color = Color.Black,
            radius = 6f,
            center = Offset(x, y)
        )
        return
    }
    
    val path = Path()
    val fillPath = Path()
    val points = mutableListOf<Offset>()

    data.forEachIndexed { index, summary ->
        val x = padding + (index.toFloat() / (data.size - 1).toFloat()) * chartWidth
        val y = padding + chartHeight - (summary.totalRevenue.toFloat() / maxValue.toFloat()) * chartHeight
        points.add(Offset(x, y))
    }
    
    // Create the line path
    if (points.isNotEmpty()) {
        path.moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }
    }
    
    // Create the fill path
    if (points.isNotEmpty()) {
        fillPath.moveTo(points.first().x, padding + chartHeight)
        fillPath.lineTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            fillPath.lineTo(points[i].x, points[i].y)
        }
        fillPath.lineTo(points.last().x, padding + chartHeight)
        fillPath.close()
    }
    
    // Draw fill area with gradient effect
    drawPath(
        path = fillPath,
        color = primaryColor.copy(alpha = 0.15f)
    )
    
    // Draw main line with better visibility
    drawPath(
        path = path,
        color = primaryColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = 4f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
    )
    
    // Draw points with better visibility (after the line so they appear on top)
    points.forEach { point ->
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = point
        )
        drawCircle(
            color = Color.Black,
            radius = 4f,
            center = point
        )
    }
}

private fun DrawScope.drawPlaceholderChart(
    width: Float,
    height: Float,
    padding: Float,
    color: Color
) {
    val chartWidth = width - (padding * 2)
    val chartHeight = height - (padding * 2)
    
    // Draw sample data points
    val sampleData = listOf(0.2f, 0.7f, 0.4f, 0.9f, 0.6f)
    val path = Path()
    
    sampleData.forEachIndexed { index, value ->
        val x = padding + (index.toFloat() / (sampleData.size - 1).toFloat()) * chartWidth
        val y = padding + chartHeight - (value * chartHeight)
        
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        
        drawCircle(
            color = Color.White,
            radius = 5f,
            center = Offset(x, y)
        )
        drawCircle(
            color = color.copy(alpha = 0.7f),
            radius = 3f,
            center = Offset(x, y)
        )
    }
    
    drawPath(
        path = path,
        color = color.copy(alpha = 0.6f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = 3f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawGridLines(
    width: Float,
    height: Float,
    padding: Float,
    color: Color
) {
    val chartWidth = width - (padding * 2)
    val chartHeight = height - (padding * 2)
    
    // Horizontal grid lines
    for (i in 0..4) {
        val y = padding + (i * chartHeight / 4)
        drawLine(
            color = color,
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawAxes(
    width: Float,
    height: Float,
    padding: Float,
    color: Color
) {
    val chartWidth = width - (padding * 2)
    val chartHeight = height - (padding * 2)
    
    // X axis
    drawLine(
        color = color,
        start = Offset(padding, padding + chartHeight),
        end = Offset(padding + chartWidth, padding + chartHeight),
        strokeWidth = 2f
    )
    
    // Y axis
    drawLine(
        color = color,
        start = Offset(padding, padding),
        end = Offset(padding, padding + chartHeight),
        strokeWidth = 2f
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