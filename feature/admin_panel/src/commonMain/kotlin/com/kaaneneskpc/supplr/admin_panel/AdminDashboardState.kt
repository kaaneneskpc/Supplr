package com.kaaneneskpc.supplr.admin_panel

import com.kaaneneskpc.supplr.data.domain.DashboardAnalytics
import com.kaaneneskpc.supplr.data.domain.DateRange


data class AdminDashboardState(
    val isLoading: Boolean = false,
    val dashboardAnalytics: DashboardAnalytics? = null,
    val selectedDateRange: DateRange = DateRange.lastWeek(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
) {
    val hasData: Boolean get() = dashboardAnalytics != null
    val hasError: Boolean get() = errorMessage != null
}


enum class DateRangeOption(
    val displayName: String,
    val dateRange: DateRange
) {
    TODAY("Today", DateRange.today()),
    LAST_WEEK("Last 7 Days", DateRange.lastWeek()),
    LAST_MONTH("Last 30 Days", DateRange.lastMonth());
    
    companion object {
        fun fromDateRange(dateRange: DateRange): DateRangeOption? {
            return values().find { it.dateRange == dateRange }
        }
    }
}


sealed class AdminDashboardEvent {
    data class DateRangeChanged(val dateRange: DateRange) : AdminDashboardEvent()
    object RefreshData : AdminDashboardEvent()
    object ClearError : AdminDashboardEvent()
} 