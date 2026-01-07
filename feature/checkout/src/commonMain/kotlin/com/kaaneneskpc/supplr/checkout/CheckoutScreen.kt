package com.kaaneneskpc.supplr.checkout

import ContentWithMessageBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.checkout.component.CouponInputSection
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.component.SupplrButton
import com.kaaneneskpc.supplr.shared.fonts.Alpha
import com.kaaneneskpc.supplr.shared.fonts.CategoryGreen
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextWhite
import com.kaaneneskpc.supplr.shared.util.formatPrice
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun CheckoutScreen(
    totalAmount: Double,
    navigateBack: () -> Unit,
    navigateToPaymentCompleted: (Boolean?, String?) -> Unit,
) {
    val messageBarState = rememberMessageBarState()
    val checkoutViewModel = koinViewModel<CheckoutViewModel>()
    val screenState = checkoutViewModel.screenState

    var clientSecret by remember { mutableStateOf("") }
    var showStripePayment by remember { mutableStateOf(false) }

    val finalAmount = checkoutViewModel.calculateFinalAmount(totalAmount)
    val hasDiscount = screenState.appliedCoupon != null

    CommonScaffold(
        title = "Checkout",
        navigateBack = navigateBack,
        actions = {
            Column(horizontalAlignment = Alignment.End) {
                if (hasDiscount) {
                    Text(
                        text = "$${formatPrice(totalAmount)}",
                        fontSize = FontSize.SMALL,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimary.copy(alpha = Alpha.HALF),
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                Text(
                    text = "$${formatPrice(finalAmount)}",
                    fontSize = FontSize.EXTRA_MEDIUM,
                    fontWeight = FontWeight.Medium,
                    color = if (hasDiscount) CategoryGreen else TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    ) { paddingValues ->
        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 12.dp,
                        bottom = 24.dp
                    )
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    OrderSummarySection(
                        customerName = "${screenState.firstName} ${screenState.lastName}",
                        email = screenState.email,
                        address = buildAddressString(
                            screenState.address,
                            screenState.city,
                            screenState.country.name
                        ),
                        phoneNumber = screenState.phoneNumber?.let { "+${it.dialCode} ${it.number}" }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SurfaceLighter)
                    Spacer(modifier = Modifier.height(16.dp))
                    CouponInputSection(
                        couponCode = screenState.couponCode,
                        onCouponCodeChange = checkoutViewModel::updateCouponCode,
                        appliedCoupon = screenState.appliedCoupon,
                        couponDiscount = screenState.couponDiscount,
                        isValidating = screenState.isValidatingCoupon,
                        error = screenState.couponError,
                        onApplyCoupon = { checkoutViewModel.applyCoupon(totalAmount) },
                        onRemoveCoupon = checkoutViewModel::removeCoupon
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SurfaceLighter)
                    Spacer(modifier = Modifier.height(16.dp))
                    PriceSummarySection(
                        subtotal = totalAmount,
                        discount = screenState.couponDiscount,
                        total = finalAmount
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Column {
                    SupplrButton(
                        text = if (screenState.isCreatingPaymentIntent) "Processing..." else "Pay with Card",
                        icon = Resources.Icon.Dollar,
                        enabled = !screenState.isCreatingPaymentIntent,
                        onClick = {
                            checkoutViewModel.createPaymentIntent(
                                onSuccess = { secret ->
                                    clientSecret = secret
                                    showStripePayment = true
                                },
                                onError = { message ->
                                    navigateToPaymentCompleted(null, message)
                                }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SupplrButton(
                        text = "Pay on Delivery",
                        icon = Resources.Icon.ShoppingCart,
                        secondary = true,
                        onClick = {
                            checkoutViewModel.payOnDelivery(
                                onSuccess = {
                                    navigateToPaymentCompleted(true, null)
                                },
                                onError = { message ->
                                    navigateToPaymentCompleted(null, message)
                                }
                            )
                        }
                    )
                    screenState.paymentIntentError?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = SurfaceError,
                            fontSize = FontSize.SMALL
                        )
                    }
                }
            }
        }
    }
    if (showStripePayment && clientSecret.isNotEmpty()) {
        StripePaymentScreen(
            clientSecret = clientSecret,
            onPaymentSuccess = { paymentIntentId ->
                showStripePayment = false
                checkoutViewModel.payWithStripe(
                    paymentIntentId = screenState.paymentIntent?.id ?: "",
                    onSuccess = {
                        navigateToPaymentCompleted(true, null)
                    },
                    onError = { message ->
                        navigateToPaymentCompleted(null, message)
                    }
                )
            },
            onPaymentFailure = { error ->
                showStripePayment = false
                navigateToPaymentCompleted(null, error)
            },
            onPaymentCanceled = {
                showStripePayment = false
            }
        )
    }
}

@Composable
private fun OrderSummarySection(
    customerName: String,
    email: String,
    address: String?,
    phoneNumber: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SurfaceLighter,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Delivery Information",
            fontSize = FontSize.MEDIUM,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        InfoRow(label = "Name", value = customerName)
        Spacer(modifier = Modifier.height(8.dp))
        if (!address.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Address", value = address)
        }
        if (!phoneNumber.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Phone", value = phoneNumber)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = FontSize.REGULAR,
            color = TextPrimary.copy(alpha = Alpha.HALF)
        )
        Text(
            text = value,
            fontSize = FontSize.REGULAR,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun PriceSummarySection(
    subtotal: Double,
    discount: Double,
    total: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SurfaceLighter,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Order Summary",
            fontSize = FontSize.MEDIUM,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        PriceRow(label = "Subtotal", value = subtotal)
        if (discount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            PriceRow(label = "Discount", value = -discount, isDiscount = true)
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Surface)
        Spacer(modifier = Modifier.height(8.dp))
        PriceRow(label = "Total", value = total, isTotal = true)
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: Double,
    isDiscount: Boolean = false,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) FontSize.MEDIUM else FontSize.REGULAR,
            fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal,
            color = TextPrimary.copy(alpha = if (isTotal) 1f else Alpha.HALF)
        )
        Text(
            text = if (isDiscount) "-$${formatPrice(-value)}" else "$${formatPrice(value)}",
            fontSize = if (isTotal) FontSize.MEDIUM else FontSize.REGULAR,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
            color = when {
                isDiscount -> CategoryGreen
                isTotal -> TextPrimary
                else -> TextPrimary
            }
        )
    }
}

private fun buildAddressString(address: String?, city: String?, country: String): String? {
    val parts = listOfNotNull(address, city, country).filter { it.isNotBlank() }
    return if (parts.isNotEmpty()) parts.joinToString(", ") else null
}