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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.data.domain.TopSellingProduct
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlue
import com.kaaneneskpc.supplr.shared.fonts.CategoryGreen
import com.kaaneneskpc.supplr.shared.fonts.CategoryRed
import com.kaaneneskpc.supplr.shared.fonts.CategoryYellow
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import org.jetbrains.compose.resources.painterResource

@Composable
fun EnhancedTopSellingProducts(
    products: List<TopSellingProduct>,
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
                    text = "Top Selling Products",
                    fontFamily = BebasNeueFont(),
                    fontSize = FontSize.LARGE,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${products.size} items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (products.isEmpty()) {
                EmptyProductState()
            } else {
                products.take(5).forEachIndexed { index, product ->
                    EnhancedProductItem(
                        product = product,
                        rank = index + 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (index < products.lastIndex && index < 4) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedProductItem(
    product: TopSellingProduct,
    rank: Int,
    modifier: Modifier = Modifier
) {
    val rankColor = when (rank) {
        1 -> CategoryYellow
        2 -> CategoryBlue
        3 -> CategoryRed
        else -> CategoryGreen
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(rankColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelMedium,
                color = rankColor,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ButtonPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Resources.Icon.ShoppingCart),
                contentDescription = product.productName,
                tint = ButtonPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.productName,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.unitsSold} sold",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary.copy(alpha = 0.7f)
                )
                
                if (product.totalRevenue > 0) {
                    Text(
                        text = " • $${product.totalRevenue.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CategoryGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Performance Indicator
        PerformanceIndicator(unitsSold = product.unitsSold)
    }
}

@Composable
private fun PerformanceIndicator(
    unitsSold: Int
) {
    val performanceLevel = when {
        unitsSold >= 10 -> "High"
        unitsSold >= 5 -> "Medium"
        else -> "Low"
    }
    
    val color = when (performanceLevel) {
        "High" -> CategoryGreen
        "Medium" -> CategoryYellow
        else -> CategoryRed
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = performanceLevel,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyProductState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(Resources.Icon.Book),
            contentDescription = "No products",
            tint = TextPrimary.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "No product sales yet",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "Sales data will appear here once orders are placed",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
} 