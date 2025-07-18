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
) {
    val messageBarState = rememberMessageBarState()
    val checkoutViewModel = koinViewModel<CheckoutViewModel>()
    val screenState = checkoutViewModel.screenState
    val isFormValid = checkoutViewModel.isFormValid


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
                        text = "Pay with PayPal",
                        icon = Resources.Image.PaypalLogo,
                        enabled = isFormValid,
                        onClick = {
                            /*viewModel.payWithPayPal(
                                onSuccess = {

                                },
                                onError = { message ->
                                    messageBarState.addError(message)
                                }
                            )*/
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SupplrButton(
                        text = "Pay on Delivery",
                        icon = Resources.Icon.ShoppingCart,
                        secondary = true,
                        enabled = isFormValid,
                        onClick = {
                            /*viewModel.payOnDelivery(
                                onSuccess = {
                                    navigateToPaymentCompleted(true, null)
                                },
                                onError = { message ->
                                    navigateToPaymentCompleted(null, message)
                                }
                            )*/
                        }
                    )
                }
            }
        }
    }
}