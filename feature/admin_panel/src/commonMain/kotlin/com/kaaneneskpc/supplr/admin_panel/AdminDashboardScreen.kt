package com.kaaneneskpc.supplr.admin_panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.admin_panel.component.EnhancedTopSellingProducts
import com.kaaneneskpc.supplr.admin_panel.component.MetricCardsRow
import com.kaaneneskpc.supplr.admin_panel.component.RevenueChart
import com.kaaneneskpc.supplr.admin_panel.component.SimpleRevenueChart
import com.kaaneneskpc.supplr.data.domain.DashboardAnalytics
import com.kaaneneskpc.supplr.data.domain.TopSellingProduct
import com.kaaneneskpc.supplr.shared.component.ErrorCard
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navigateBack: () -> Unit,
    viewModel: AdminDashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analytics Dashboard",
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.EXTRA_LARGE,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back",
                            tint = IconPrimary
                        )
                    }
                },
                actions = {
                    // Date Range Selector
                    DateRangeSelectorButton(
                        selectedDateRange = state.selectedDateRange,
                        onDateRangeChanged = { dateRange ->
                            viewModel.onEvent(AdminDashboardEvent.DateRangeChanged(dateRange))
                        }
                    )
                    
                    // Refresh Button
                    IconButton(
                        onClick = { viewModel.onEvent(AdminDashboardEvent.RefreshData) }
                    ) {
                        Icon(
                            painter = painterResource(Resources.Icon.Book), // Geçici icon, refresh icon eklenebilir
                            contentDescription = "Refresh",
                            tint = IconPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> {
                    LoadingContent()
                }
                state.hasError -> {
                    ErrorContent(
                        errorMessage = state.errorMessage ?: "Unknown error",
                        onRetry = { viewModel.onEvent(AdminDashboardEvent.RefreshData) },
                        onDismiss = { viewModel.onEvent(AdminDashboardEvent.ClearError) }
                    )
                }
                state.hasData -> {
                    DashboardContent(
                        analytics = state.dashboardAnalytics!!,
                        isRefreshing = state.isRefreshing
                    )
                }
                else -> {
                    EmptyContent()
                }
            }
        }
    }
}

@Composable
private fun DateRangeSelectorButton(
    selectedDateRange: com.kaaneneskpc.supplr.data.domain.DateRange,
    onDateRangeChanged: (com.kaaneneskpc.supplr.data.domain.DateRange) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = DateRangeOption.fromDateRange(selectedDateRange) ?: DateRangeOption.LAST_WEEK

    Box {
        TextButton(
            onClick = { expanded = true }
        ) {
            Text(
                text = selectedOption.displayName,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DateRangeOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = option.displayName,
                            color = if (option == selectedOption) ButtonPrimary else TextPrimary
                        )
                    },
                    onClick = {
                        onDateRangeChanged(option.dateRange)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = ButtonPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading analytics...",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ErrorCard(
            message = errorMessage,
            modifier = Modifier.padding(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRetry,
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = ButtonPrimary
                )
            ) {
                Text("Retry")
            }
            
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Dismiss",
                    color = TextPrimary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No data available",
            color = TextPrimary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DashboardContent(
    analytics: DashboardAnalytics,
    isRefreshing: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Loading indicator for refresh
        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ButtonPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        
        // Enhanced Key Metrics Row
        MetricCardsRow(
            totalRevenue = analytics.totalRevenue,
            totalOrders = analytics.totalOrders,
            averageOrderValue = analytics.averageOrderValue
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Line Chart (Custom Canvas)
        RevenueChart(
            dailySummaries = analytics.dailySummaries,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bar Chart (Progress Bars)
        SimpleRevenueChart(
            dailySummaries = analytics.dailySummaries,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Enhanced Top Selling Products
        EnhancedTopSellingProducts(
            products = analytics.topSellingProducts,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // User Statistics
        UserStatisticsSection(userStats = analytics.userStats)
    }
}

@Composable
private fun KeyMetricsSection(analytics: DashboardAnalytics) {
    Text(
        text = "Key Metrics",
        fontFamily = BebasNeueFont(),
        fontSize = FontSize.LARGE,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            title = "Total Revenue",
            value = "$${analytics.totalRevenue.toInt()}",
            modifier = Modifier.weight(1f)
        )
        
        MetricCard(
            title = "Total Orders",
            value = analytics.totalOrders.toString(),
            modifier = Modifier.weight(1f)
        )
        
        MetricCard(
            title = "Avg Order Value",
            value = "$${analytics.averageOrderValue.toInt()}",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = BebasNeueFont(),
                fontSize = FontSize.MEDIUM,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TopSellingProductsSection(products: List<TopSellingProduct>) {
    Text(
        text = "Top Selling Products",
        fontFamily = BebasNeueFont(),
        fontSize = FontSize.LARGE,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    
    if (products.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Text(
                text = "No product data available",
                modifier = Modifier.padding(16.dp),
                color = TextPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        products.take(5).forEach { product ->
            ProductItem(product = product)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProductItem(product: TopSellingProduct) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Units sold: ${product.unitsSold}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun UserStatisticsSection(userStats: com.kaaneneskpc.supplr.data.domain.UserStats) {
    Text(
        text = "User Statistics",
        fontFamily = BebasNeueFont(),
        fontSize = FontSize.LARGE,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            title = "Total Users",
            value = userStats.totalUsers.toString(),
            modifier = Modifier.weight(1f)
        )
        
        MetricCard(
            title = "New Today",
            value = userStats.newUsersToday.toString(),
            modifier = Modifier.weight(1f)
        )
        
        MetricCard(
            title = "New This Week",
            value = userStats.newUsersThisWeek.toString(),
            modifier = Modifier.weight(1f)
        )
    }
} 