package com.kaaneneskpc.supplr.order_history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.OrderRepository
import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderHistoryViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {
    var ordersState: RequestState<List<Order>> by mutableStateOf(RequestState.Loading)
        private set
    var selectedOrderState: RequestState<Order?> by mutableStateOf(RequestState.Idle)
        private set

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            ordersState = RequestState.Loading
            orderRepository.getOrdersByCustomerId()
                .collectLatest { result ->
                    ordersState = result
                }
        }
    }

    fun loadOrderById(orderId: String) {
        viewModelScope.launch {
            selectedOrderState = RequestState.Loading
            orderRepository.getOrderById(orderId)
                .collectLatest { result ->
                    selectedOrderState = result
                }
        }
    }

    fun refreshOrders() {
        loadOrders()
    }
}
