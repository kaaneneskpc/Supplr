package com.kaaneneskpc.supplr.data.domain

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class DailySummary(
    val date: String,
    val totalRevenue: Double,
    val orderCount: Int,
    val averageOrderValue: Double = if (orderCount > 0) totalRevenue / orderCount else 0.0
)


@Serializable
data class TopSellingProduct(
    val productId: String,
    val productName: String,
    val unitsSold: Int,
    val totalRevenue: Double,
    val thumbnail: String? = null
)


@Serializable
data class UserStats(
    val totalUsers: Int,
    val newUsersToday: Int,
    val newUsersThisWeek: Int,
    val newUsersThisMonth: Int
)


@Serializable
data class DashboardAnalytics(
    val totalRevenue: Double,
    val totalOrders: Int,
    val averageOrderValue: Double,
    val topSellingProducts: List<TopSellingProduct>,
    val dailySummaries: List<DailySummary>,
    val userStats: UserStats
)


@Serializable
data class DateRange(
    val startDate: Long,
    val endDate: Long
) {
    companion object {
        fun today(): DateRange {
            val now = Clock.System.now().toEpochMilliseconds()
            val startOfDay = now - (now % (24 * 60 * 60 * 1000))
            val endOfDay = startOfDay + (24 * 60 * 60 * 1000) - 1
            return DateRange(startOfDay, endOfDay)
        }
        
        fun lastWeek(): DateRange {
            val now = Clock.System.now().toEpochMilliseconds()
            val weekAgo = now - (7 * 24 * 60 * 60 * 1000)
            return DateRange(weekAgo, now)
        }
        
        fun lastMonth(): DateRange {
            val now = Clock.System.now().toEpochMilliseconds()
            val monthAgo = now - (30 * 24 * 60 * 60 * 1000)
            return DateRange(monthAgo, now)
        }
    }
} 