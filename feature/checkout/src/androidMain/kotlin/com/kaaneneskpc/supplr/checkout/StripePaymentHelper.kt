package com.kaaneneskpc.supplr.checkout

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kaaneneskpc.supplr.shared.Consts
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetContract
import com.stripe.android.paymentsheet.PaymentSheetResult

@Composable
fun StripePaymentHandler(
    clientSecret: String,
    onPaymentSuccess: (String) -> Unit,
    onPaymentFailure: (String) -> Unit,
    onPaymentCanceled: () -> Unit
) {
    val context = LocalContext.current
    var shouldLaunchPayment by remember { mutableStateOf(false) }
    
    // Initialize Stripe
    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, Consts.STRIPE_PUBLISH_KEY)
    }
    
    // PaymentSheet launcher - registered at composition time (lifecycle safe)
    val paymentSheetLauncher = rememberLauncherForActivityResult(
        contract = PaymentSheetContract()
    ) { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                onPaymentSuccess(clientSecret)
            }
            is PaymentSheetResult.Failed -> {
                onPaymentFailure(result.error.message ?: "Payment failed")
            }
            is PaymentSheetResult.Canceled -> {
                onPaymentCanceled()
            }
        }
    }
    
    // Trigger payment when clientSecret is available
    LaunchedEffect(clientSecret) {
        if (clientSecret.isNotEmpty()) {
            shouldLaunchPayment = true
        }
    }
    
    // Launch payment sheet
    LaunchedEffect(shouldLaunchPayment) {
        if (shouldLaunchPayment && clientSecret.isNotEmpty()) {
            shouldLaunchPayment = false // Prevent multiple launches
            
            val configuration = PaymentSheet.Configuration(
                merchantDisplayName = "Supplr"
            )
            
            paymentSheetLauncher.launch(
                PaymentSheetContract.Args.createPaymentIntentArgs(
                    clientSecret = clientSecret,
                    config = configuration
                )
            )
        }
    }
    
    // Show processing state
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Processing payment...")
    }
}