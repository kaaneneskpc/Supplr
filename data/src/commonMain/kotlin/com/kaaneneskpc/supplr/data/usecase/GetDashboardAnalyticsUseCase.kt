package com.kaaneneskpc.supplr.data.usecase

import com.kaaneneskpc.supplr.data.domain.AdminRepository
import com.kaaneneskpc.supplr.data.domain.DashboardAnalytics
import com.kaaneneskpc.supplr.data.domain.DailySummary
import com.kaaneneskpc.supplr.data.domain.DateRange
import com.kaaneneskpc.supplr.data.domain.TopSellingProduct

import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetDashboardAnalyticsUseCase(
    private val adminRepository: AdminRepository
) {
    suspend operator fun invoke(dateRange: DateRange): RequestState<DashboardAnalytics> {
        return try {
            println("📊 Analytics Request: ${dateRange.startDate} to ${dateRange.endDate}")
            
            // Siparişleri çek
            val ordersResult = adminRepository.getOrdersByDateRange(
                dateRange.startDate, 
                dateRange.endDate
            )
            
            when (ordersResult) {
                is RequestState.Success -> {
                    val orders = ordersResult.data
                    println("📦 Orders Found: ${orders.size}")
                    orders.forEach { order ->
                        println("  - Order: ${order.orderId}, Amount: ${order.totalAmount}, Items: ${order.items.size}")
                    }
                    val analytics = calculateAnalytics(orders, dateRange)
                    RequestState.Success(analytics)
                }
                is RequestState.Error -> {
                    RequestState.Error(ordersResult.message)
                }
                else -> {
                    RequestState.Loading
                }
            }
        } catch (e: Exception) {
            RequestState.Error("Analytics calculation failed: ${e.message}")
        }
    }
    
    private suspend fun calculateAnalytics(orders: List<Order>, dateRange: DateRange): DashboardAnalytics {
        // Toplam gelir hesapla (Order'daki totalAmount alanından)
        val totalRevenue = orders.sumOf { it.totalAmount }
        
        // Toplam sipariş sayısı
        val totalOrders = orders.size
        
        // Ortalama sipariş değeri
        val averageOrderValue = if (totalOrders > 0) totalRevenue / totalOrders else 0.0
        
        // En çok satan ürünleri hesapla
        val topSellingProducts = calculateTopSellingProducts(orders)
        
        // Günlük özet verileri hesapla
        val dailySummaries = calculateDailySummaries(orders)
        
        // Kullanıcı istatistiklerini al
        val userStatsResult = getUserStats()
        val userStats = when (userStatsResult) {
            is RequestState.Success -> userStatsResult.data
            else -> com.kaaneneskpc.supplr.data.domain.UserStats(0, 0, 0, 0)
        }
        
        return DashboardAnalytics(
            totalRevenue = totalRevenue,
            totalOrders = totalOrders,
            averageOrderValue = averageOrderValue,
            topSellingProducts = topSellingProducts,
            dailySummaries = dailySummaries,
            userStats = userStats
        )
    }
    
    private fun calculateTopSellingProducts(orders: List<Order>): List<TopSellingProduct> {
        val productStats = mutableMapOf<String, ProductStat>()
        
        orders.forEach { order ->
            order.items.forEach { item ->
                val productId = item.productId
                val existingStat = productStats[productId]
                
                if (existingStat != null) {
                    productStats[productId] = existingStat.copy(
                        unitsSold = existingStat.unitsSold + item.quantity
                    )
                } else {
                    productStats[productId] = ProductStat(
                        productId = productId,
                        unitsSold = item.quantity
                    )
                }
            }
        }
        
        return productStats.values
            .sortedByDescending { it.unitsSold } // Satılan miktar bazında sırala
            .take(10) // En çok satan 10 ürün
            .map { stat ->
                TopSellingProduct(
                    productId = stat.productId,
                    productName = "Product ${stat.productId}", // Geçici isim, ileride product detayları çekilebilir
                    unitsSold = stat.unitsSold,
                    totalRevenue = 0.0, // CartItem'da fiyat bilgisi olmadığı için 0
                    thumbnail = null // CartItem'da thumbnail bilgisi yok
                )
            }
    }
    
    private fun calculateDailySummaries(orders: List<Order>): List<DailySummary> {
        val dailyStats = mutableMapOf<String, DailyOrderStats>()
        
        orders.forEach { order ->
            val date = formatDateFromTimestamp(order.createdAt)
            val existingStat = dailyStats[date]
            
            if (existingStat != null) {
                dailyStats[date] = existingStat.copy(
                    totalRevenue = existingStat.totalRevenue + order.totalAmount,
                    orderCount = existingStat.orderCount + 1
                )
            } else {
                dailyStats[date] = DailyOrderStats(
                    totalRevenue = order.totalAmount,
                    orderCount = 1
                )
            }
        }
        
        return dailyStats.map { (date, stats) ->
            DailySummary(
                date = date,
                totalRevenue = stats.totalRevenue,
                orderCount = stats.orderCount
            )
        }.sortedBy { it.date }
    }
    
    private suspend fun getUserStats(): RequestState<com.kaaneneskpc.supplr.data.domain.UserStats> {
        return try {
            val usersResult = adminRepository.getAllUsers()
            when (usersResult) {
                is RequestState.Success -> {
                    val users = usersResult.data
                    val now = Clock.System.now().toEpochMilliseconds()
                    
                    // Zaman aralıklarını hesapla
                    val oneDayAgo = now - (24 * 60 * 60 * 1000)
                    val oneWeekAgo = now - (7 * 24 * 60 * 60 * 1000)
                    val oneMonthAgo = now - (30 * 24 * 60 * 60 * 1000)
                    
                    // Kullanıcıları createdAt tarihine göre filtrele
                    val newUsersToday = users.count { user -> 
                        user.createdAt >= oneDayAgo 
                    }
                    
                    val newUsersThisWeek = users.count { user -> 
                        user.createdAt >= oneWeekAgo 
                    }
                    
                    val newUsersThisMonth = users.count { user -> 
                        user.createdAt >= oneMonthAgo 
                    }
                    
                    val userStats = com.kaaneneskpc.supplr.data.domain.UserStats(
                        totalUsers = users.size,
                        newUsersToday = newUsersToday,
                        newUsersThisWeek = newUsersThisWeek,
                        newUsersThisMonth = newUsersThisMonth
                    )
                    
                    RequestState.Success(userStats)
                }
                is RequestState.Error -> {
                    RequestState.Error(usersResult.message)
                }
                else -> {
                    RequestState.Loading
                }
            }
        } catch (e: Exception) {
            RequestState.Error("User stats calculation failed: ${e.message}")
        }
    }
    
    private fun formatDateFromTimestamp(timestamp: Long): String {
        return try {
            val instant = Instant.fromEpochMilliseconds(timestamp)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
        } catch (e: Exception) {
            "1970-01-01" // Fallback date
        }
    }
    
    private data class ProductStat(
        val productId: String,
        val unitsSold: Int
    )
    
    private data class DailyOrderStats(
        val totalRevenue: Double,
        val orderCount: Int
    )
} 