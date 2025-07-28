package com.kaaneneskpc.supplr.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.data.domain.OrderRepository
import com.kaaneneskpc.supplr.data.domain.PaymentRepository
import com.kaaneneskpc.supplr.shared.domain.CartItem
import com.kaaneneskpc.supplr.shared.domain.Country
import com.kaaneneskpc.supplr.shared.domain.Customer
import com.kaaneneskpc.supplr.shared.domain.Order
import com.kaaneneskpc.supplr.shared.domain.PaymentIntentResponse
import com.kaaneneskpc.supplr.shared.domain.PhoneNumber
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.ranges.contains

data class CheckoutScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val country: Country = Country.Serbia,
    val phoneNumber: PhoneNumber? = null,
    val cart: List<CartItem> = emptyList(),
    val paymentIntent: PaymentIntentResponse? = null,
    val isCreatingPaymentIntent: Boolean = false,
    val paymentIntentError: String? = null
)

class CheckoutViewModel(
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: CheckoutScreenState by mutableStateOf(CheckoutScreenState())
        private set

    val isFormValid: Boolean
        get() = with(screenState) {
            firstName.length in 3..50 &&
                    lastName.length in 3..50 &&
                    city?.length in 3..50 &&
                    postalCode != null || postalCode?.toString()?.length in 3..8 &&
                    address?.length in 3..50 &&
                    phoneNumber?.number?.length in 5..30
        }

    init {
        viewModelScope.launch {
            customerRepository.readMeCustomerFlow().collectLatest { data ->
                if (data.isSuccess()) {
                    val fetchedCustomer = data.getSuccessData()
                    screenState = CheckoutScreenState(
                        id = fetchedCustomer.id,
                        firstName = fetchedCustomer.firstName,
                        lastName = fetchedCustomer.lastName,
                        email = fetchedCustomer.email,
                        city = fetchedCustomer.city,
                        postalCode = fetchedCustomer.postalCode,
                        address = fetchedCustomer.address,
                        phoneNumber = fetchedCustomer.phoneNumber,
                        country = Country.entries.firstOrNull { it.dialCode == fetchedCustomer.phoneNumber?.dialCode }
                            ?: Country.Serbia,
                        cart = fetchedCustomer.cart
                    )
                    screenReady = RequestState.Success(Unit)
                } else if (data.isError()) {
                    screenReady = RequestState.Error(data.getErrorMessage())
                }
            }
        }
    }

    fun updateFirstName(value: String) {
        screenState = screenState.copy(firstName = value)
    }

    fun updateLastName(value: String) {
        screenState = screenState.copy(lastName = value)
    }

    fun updateCity(value: String) {
        screenState = screenState.copy(city = value)
    }

    fun updatePostalCode(value: Int?) {
        screenState = screenState.copy(postalCode = value)
    }

    fun updateAddress(value: String) {
        screenState = screenState.copy(address = value)
    }

    fun updateCountry(value: Country) {
        screenState = screenState.copy(
            country = value,
            phoneNumber = screenState.phoneNumber?.copy(
                dialCode = value.dialCode
            )
        )
    }

    fun updatePhoneNumber(value: String) {
        screenState = screenState.copy(
            phoneNumber = PhoneNumber(
                dialCode = screenState.country.dialCode,
                number = value
            )
        )
    }

    private fun updateCustomer(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            customerRepository.updateCustomer(
                customer = Customer(
                    id = screenState.id,
                    firstName = screenState.firstName,
                    lastName = screenState.lastName,
                    email = screenState.email,
                    city = screenState.city,
                    postalCode = screenState.postalCode,
                    address = screenState.address,
                    phoneNumber = screenState.phoneNumber
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun payOnDelivery(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        updateCustomer(
            onSuccess = {
                createOrder(
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError = onError
        )
    }

    fun createPaymentIntent(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            screenState = screenState.copy(
                isCreatingPaymentIntent = true,
                paymentIntentError = null
            )
            
            val totalAmount = savedStateHandle.get<String>("totalAmount")?.toDoubleOrNull() ?: 0.0
            val amountInCents = (totalAmount * 100).toLong()
            
            val result = paymentRepository.createPaymentIntent(
                amount = amountInCents,
                currency = "usd"
            )
            
            result.fold(
                onSuccess = { paymentIntentResponse ->
                    screenState = screenState.copy(
                        paymentIntent = paymentIntentResponse,
                        isCreatingPaymentIntent = false
                    )
                    onSuccess(paymentIntentResponse.client_secret)
                },
                onFailure = { error ->
                    screenState = screenState.copy(
                        isCreatingPaymentIntent = false,
                        paymentIntentError = error.message ?: "Failed to create payment intent"
                    )
                    onError(error.message ?: "Failed to create payment intent")
                }
            )
        }
    }

    fun payWithStripe(
        paymentIntentId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        updateCustomer(
            onSuccess = {
                createOrderWithPayment(
                    paymentIntentId = paymentIntentId,
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError = onError
        )
    }

    private fun createOrder(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            orderRepository.createTheOrder(
                order = Order(
                    customerId = screenState.id,
                    items = screenState.cart,
                    totalAmount = savedStateHandle.get<String>("totalAmount")?.toDoubleOrNull() ?: 0.0,
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    private fun createOrderWithPayment(
        paymentIntentId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val order = Order(
                customerId = screenState.id,
                items = screenState.cart,
                totalAmount = savedStateHandle.get<String>("totalAmount")?.toDoubleOrNull() ?: 0.0,
                currency = "usd",
                paymentIntentId = paymentIntentId,
                status = "CONFIRMED",
                shippingAddress = "${screenState.address}, ${screenState.city}, ${screenState.country.name}"
            )
            
            val result = paymentRepository.saveOrder(order)
            result.fold(
                onSuccess = { orderId ->
                    customerRepository.deleteAllCartItems(
                        onSuccess = { onSuccess() },
                        onError = { onSuccess() }
                    )
                },
                onFailure = { error ->
                    onError(error.message ?: "Failed to save order")
                }
            )
        }
    }
}