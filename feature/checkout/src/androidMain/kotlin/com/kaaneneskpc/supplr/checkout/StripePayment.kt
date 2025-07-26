package com.kaaneneskpc.supplr.checkout

import androidx.compose.runtime.Composable

@Composable
actual fun StripePaymentScreen(
    clientSecret: String,
    onPaymentSuccess: (String) -> Unit,
    onPaymentFailure: (String) -> Unit,
    onPaymentCanceled: () -> Unit
) {
    StripePaymentHandler(
        clientSecret = clientSecret,
        onPaymentSuccess = onPaymentSuccess,
        onPaymentFailure = onPaymentFailure,
        onPaymentCanceled = onPaymentCanceled
    )
} 