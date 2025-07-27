package com.kaaneneskpc.supplr.admin_panel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.DateRange
import com.kaaneneskpc.supplr.data.usecase.GetDashboardAnalyticsUseCase
import com.kaaneneskpc.supplr.data.usecase.GetUserStatisticsUseCase
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminDashboardViewModel(
    private val getDashboardAnalyticsUseCase: GetDashboardAnalyticsUseCase,
    private val getUserStatisticsUseCase: GetUserStatisticsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminDashboardState())
    val state: StateFlow<AdminDashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun onEvent(event: AdminDashboardEvent) {
        when (event) {
            is AdminDashboardEvent.DateRangeChanged -> {
                _state.value = _state.value.copy(
                    selectedDateRange = event.dateRange,
                    errorMessage = null
                )
                loadDashboardData(event.dateRange)
            }
            is AdminDashboardEvent.RefreshData -> {
                refreshData()
            }
            is AdminDashboardEvent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun loadDashboardData(dateRange: DateRange = _state.value.selectedDateRange) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val analyticsResult = getDashboardAnalyticsUseCase(dateRange)
                
                when (analyticsResult) {
                    is RequestState.Success -> {
                        val data = analyticsResult.data
                        println("✅ Analytics Success: Revenue=${data.totalRevenue}, Orders=${data.totalOrders}, Users=${data.userStats.totalUsers}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            dashboardAnalytics = data,
                            errorMessage = null
                        )
                    }
                    is RequestState.Error -> {
                        println("❌ Analytics Error: ${analyticsResult.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = analyticsResult.message
                        )
                    }
                    is RequestState.Loading -> {
                        // State zaten loading olarak set edildi
                    }
                    is RequestState.Idle -> {
                        // İlk yükleme durumu
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
                errorMessage = null
            )

            try {
                val analyticsResult = getDashboardAnalyticsUseCase(_state.value.selectedDateRange)
                
                when (analyticsResult) {
                    is RequestState.Success -> {
                        _state.value = _state.value.copy(
                            isRefreshing = false,
                            dashboardAnalytics = analyticsResult.data,
                            errorMessage = null
                        )
                    }
                    is RequestState.Error -> {
                        _state.value = _state.value.copy(
                            isRefreshing = false,
                            errorMessage = analyticsResult.message
                        )
                    }
                    else -> {
                        _state.value = _state.value.copy(isRefreshing = false)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRefreshing = false,
                    errorMessage = "Refresh failed: ${e.message}"
                )
            }
        }
    }
} 