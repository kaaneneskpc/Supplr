package com.kaaneneskpc.supplr.admin_panel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.OrderRepository
import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.domain.OrderStatus
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminOrdersViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    val allOrders = orderRepository.getAllOrders().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

    var selectedOrderState: RequestState<Order?> by mutableStateOf(RequestState.Idle)
        private set

    var isUpdating by mutableStateOf(false)
        private set

    fun loadOrderById(orderId: String) {
        viewModelScope.launch {
            selectedOrderState = RequestState.Loading
            orderRepository.getOrderById(orderId).collect { result ->
                selectedOrderState = result
            }
        }
    }

    fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isUpdating) return
        viewModelScope.launch {
            isUpdating = true
            orderRepository.updateOrderStatus(
                orderId = orderId,
                newStatus = newStatus,
                note = "Status updated by admin",
                onSuccess = {
                    isUpdating = false
                    onSuccess()
                },
                onError = { error ->
                    isUpdating = false
                    onError(error)
                }
            )
        }
    }

    fun cancelOrder(
        orderId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        updateOrderStatus(
            orderId = orderId,
            newStatus = OrderStatus.CANCELLED,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun canModifyOrder(order: Order): Boolean {
        val status = order.getCurrentStatus()
        return status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED
    }

    fun getNextStatus(currentStatus: OrderStatus): OrderStatus? {
        return when (currentStatus) {
            OrderStatus.PENDING -> OrderStatus.CONFIRMED
            OrderStatus.CONFIRMED -> OrderStatus.PREPARING
            OrderStatus.PREPARING -> OrderStatus.SHIPPED
            OrderStatus.SHIPPED -> OrderStatus.DELIVERED
            OrderStatus.DELIVERED -> null
            OrderStatus.CANCELLED -> null
        }
    }
}
