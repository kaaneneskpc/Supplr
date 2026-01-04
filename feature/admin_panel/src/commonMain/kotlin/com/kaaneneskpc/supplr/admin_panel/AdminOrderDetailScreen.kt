package com.kaaneneskpc.supplr.admin_panel

import ContentWithMessageBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kaaneneskpc.supplr.shared.component.InfoCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.domain.OrderStatus
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.BorderIdle
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.SurfaceSecondary
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextSecondary
import com.kaaneneskpc.supplr.shared.fonts.TextWhite
import com.kaaneneskpc.supplr.shared.util.DisplayResult
import com.kaaneneskpc.supplr.shared.util.formatDate
import com.kaaneneskpc.supplr.shared.util.formatPrice
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    orderId: String,
    navigateBack: () -> Unit
) {
    val viewModel = koinViewModel<AdminOrdersViewModel>()
    val orderState = viewModel.selectedOrderState
    val isUpdating = viewModel.isUpdating
    val messageBarState = rememberMessageBarState()

    LaunchedEffect(orderId) {
        viewModel.loadOrderById(orderId)
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order Details",
                        fontSize = FontSize.LARGE,
                        fontFamily = BebasNeueFont(),
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        }
    ) { paddingValues ->
        ContentWithMessageBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            contentBackgroundColor = Color.Transparent
        ) {
            orderState.DisplayResult(
                onLoading = {
                    LoadingCard(modifier = Modifier.fillMaxSize())
                },
                onSuccess = { order ->
                    if (order == null) {
                        InfoCard(
                            image = Resources.Image.Cat,
                            title = "Order not found",
                            subtitle = "This order does not exist."
                        )
                    } else {
                        val canModify = viewModel.canModifyOrder(order)
                        val nextStatus = viewModel.getNextStatus(order.getCurrentStatus())

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                OrderInfoCard(order = order)
                            }
                            item {
                                SectionTitle(title = "Items (${order.items.sumOf { it.quantity }})")
                            }
                            items(order.items) { cartItem ->
                                OrderItemCard(
                                    title = cartItem.title,
                                    thumbnail = cartItem.thumbnail,
                                    quantity = cartItem.quantity,
                                    price = cartItem.price
                                )
                            }
                            if (order.shippingAddress.isNotBlank()) {
                                item {
                                    SectionTitle(title = "Shipping Address")
                                }
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceLighter),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = order.shippingAddress,
                                            modifier = Modifier.padding(16.dp),
                                            fontSize = FontSize.REGULAR,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                            if (canModify) {
                                item {
                                    SectionTitle(title = "Actions")
                                }
                                item {
                                    ActionButtons(
                                        nextStatus = nextStatus,
                                        isUpdating = isUpdating,
                                        onUpdateStatus = {
                                            if (nextStatus != null) {
                                                viewModel.updateOrderStatus(
                                                    orderId = order.orderId,
                                                    newStatus = nextStatus,
                                                    onSuccess = {
                                                        messageBarState.addSuccess("Status updated to ${nextStatus.displayName}")
                                                        viewModel.loadOrderById(orderId)
                                                    },
                                                    onError = { error ->
                                                        messageBarState.addError(error)
                                                    }
                                                )
                                            }
                                        },
                                        onCancelOrder = {
                                            viewModel.cancelOrder(
                                                orderId = order.orderId,
                                                onSuccess = {
                                                    messageBarState.addSuccess("Order cancelled")
                                                    viewModel.loadOrderById(orderId)
                                                },
                                                onError = { error ->
                                                    messageBarState.addError(error)
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                },
                onError = { errorMessage ->
                    InfoCard(
                        image = Resources.Image.Cat,
                        title = "Error",
                        subtitle = errorMessage
                    )
                }
            )
        }
    }
}

@Composable
private fun OrderInfoCard(order: Order) {
    val status = order.getCurrentStatus()
    val successGreen = Color(0xFF4CAF50)
    val statusColor = when (status) {
        OrderStatus.PENDING -> SurfaceSecondary
        OrderStatus.CONFIRMED -> successGreen
        OrderStatus.PREPARING -> successGreen
        OrderStatus.SHIPPED -> successGreen
        OrderStatus.DELIVERED -> successGreen
        OrderStatus.CANCELLED -> SurfaceError
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLighter),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderId.take(8)}",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${status.icon} ${status.displayName}",
                        fontSize = FontSize.EXTRA_SMALL,
                        color = TextWhite,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderIdle)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Date", value = formatDate(order.createdAt))
            InfoRow(label = "Customer ID", value = order.customerId.take(16) + "...")
            InfoRow(label = "Total", value = "$${formatPrice(order.totalAmount)}")
            if (!order.trackingNumber.isNullOrBlank()) {
                InfoRow(label = "Tracking Number", value = order.trackingNumber.orEmpty())
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = FontSize.SMALL,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = FontSize.SMALL,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = FontSize.REGULAR,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun OrderItemCard(
    title: String,
    thumbnail: String,
    quantity: Int,
    price: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLighter),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(thumbnail)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Surface),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "x$quantity",
                    fontSize = FontSize.SMALL,
                    color = TextSecondary
                )
            }
            Text(
                text = "$${formatPrice(price * quantity)}",
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ActionButtons(
    nextStatus: OrderStatus?,
    isUpdating: Boolean,
    onUpdateStatus: () -> Unit,
    onCancelOrder: () -> Unit
) {
    val successGreen = Color(0xFF4CAF50)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLighter),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (nextStatus != null) {
                Button(
                    onClick = onUpdateStatus,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(containerColor = successGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TextWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Update to: ${nextStatus.displayName}",
                            color = TextWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            OutlinedButton(
                onClick = onCancelOrder,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SurfaceError),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Cancel Order",
                    color = SurfaceError,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


