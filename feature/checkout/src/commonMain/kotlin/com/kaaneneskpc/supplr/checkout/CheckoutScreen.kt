package com.kaaneneskpc.supplr.checkout

import ContentWithMessageBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.component.ProfileForm
import com.kaaneneskpc.supplr.shared.component.SupplrButton
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextWhite
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
    val isFormValid = checkoutViewModel.isFormValid

    var clientSecret by remember { mutableStateOf("") }
    var showStripePayment by remember { mutableStateOf(false) }


    CommonScaffold(
        title = "Checkout",
        navigateBack = navigateBack,
        actions = {
            Text(
                text = "$${totalAmount}",
                fontSize = FontSize.EXTRA_MEDIUM,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
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
                ProfileForm(
                    modifier = Modifier.weight(1f),
                    country = screenState.country,
                    onCountrySelect = checkoutViewModel::updateCountry,
                    firstName = screenState.firstName,
                    onFirstNameChange = checkoutViewModel::updateFirstName,
                    lastName = screenState.lastName,
                    onLastNameChange = checkoutViewModel::updateLastName,
                    email = screenState.email,
                    city = screenState.city,
                    onCityChange = checkoutViewModel::updateCity,
                    postalCode = screenState.postalCode,
                    onPostalCodeChange = checkoutViewModel::updatePostalCode,
                    address = screenState.address,
                    onAddressChange = checkoutViewModel::updateAddress,
                    phoneNumber = screenState.phoneNumber?.number,
                    onPhoneNumberChange = checkoutViewModel::updatePhoneNumber
                )
                Column {
                    SupplrButton(
                        text = if (screenState.isCreatingPaymentIntent) "Processing..." else "Pay with Card",
                        icon = Resources.Icon.Dollar,
                        enabled = isFormValid && !screenState.isCreatingPaymentIntent,
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
                        enabled = isFormValid,
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