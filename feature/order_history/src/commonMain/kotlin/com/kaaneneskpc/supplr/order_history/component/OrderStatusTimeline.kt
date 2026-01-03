package com.kaaneneskpc.supplr.order_history.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.domain.OrderStatus
import com.kaaneneskpc.supplr.shared.domain.OrderStatusUpdate
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.SurfaceSecondary
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextSecondary
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun OrderStatusTimeline(
    statusHistory: List<OrderStatusUpdate>,
    modifier: Modifier = Modifier
) {
    val allStatuses = listOf(
        OrderStatus.PENDING,
        OrderStatus.CONFIRMED,
        OrderStatus.PREPARING,
        OrderStatus.SHIPPED,
        OrderStatus.DELIVERED
    )
    val completedStatuses = statusHistory.map { it.status }.toSet()
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(allStatuses) { index, status ->
            val isCompleted = completedStatuses.contains(status)
            val statusUpdate = statusHistory.find { it.status == status }
            val isLast = index == allStatuses.lastIndex
            TimelineItem(
                status = status,
                isCompleted = isCompleted,
                timestamp = statusUpdate?.timestamp,
                note = statusUpdate?.note,
                isLast = isLast
            )
        }
    }
}

@Composable
private fun TimelineItem(
    status: OrderStatus,
    isCompleted: Boolean,
    timestamp: Long?,
    note: String?,
    isLast: Boolean
) {
    val successGreen = Color(0xFF4CAF50)
    val circleColor = if (isCompleted) successGreen else SurfaceSecondary
    val lineColor = if (isCompleted) successGreen else SurfaceSecondary.copy(alpha = 0.3f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Canvas(
            modifier = Modifier
                .size(width = 24.dp, height = if (isLast) 24.dp else 60.dp)
        ) {
            drawCircle(
                color = circleColor,
                radius = 10.dp.toPx(),
                center = Offset(size.width / 2, 12.dp.toPx())
            )
            if (isCompleted) {
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(size.width / 2, 12.dp.toPx())
                )
            }
            if (!isLast) {
                drawLine(
                    color = lineColor,
                    start = Offset(size.width / 2, 24.dp.toPx()),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${status.icon} ${status.displayName}",
                fontSize = FontSize.REGULAR,
                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                color = if (isCompleted) TextPrimary else TextSecondary
            )
            if (timestamp != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatDateTime(timestamp),
                    fontSize = FontSize.SMALL,
                    color = TextSecondary
                )
            } else {
                Text(
                    text = "Waiting",
                    fontSize = FontSize.SMALL,
                    color = TextSecondary.copy(alpha = 0.6f)
                )
            }
            if (!note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = note,
                    fontSize = FontSize.SMALL,
                    color = TextSecondary
                )
            }
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val months = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        "${localDateTime.dayOfMonth} ${months[localDateTime.monthNumber - 1]} $hour:$minute"
    } catch (_: Exception) {
        ""
    }
}
