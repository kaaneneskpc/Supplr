package com.kaaneneskpc.supplr.checkout

import androidx.compose.runtime.Composable

@Composable
expect fun StripePaymentScreen(
    clientSecret: String,
    onPaymentSuccess: (String) -> Unit,
    onPaymentFailure: (String) -> Unit,
    onPaymentCanceled: () -> Unit
) 