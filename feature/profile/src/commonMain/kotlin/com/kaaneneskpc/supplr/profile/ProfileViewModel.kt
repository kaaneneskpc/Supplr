package com.kaaneneskpc.supplr.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.shared.domain.Country
import com.kaaneneskpc.supplr.shared.domain.Customer
import com.kaaneneskpc.supplr.shared.domain.PhoneNumber
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    var country: Country = Country.Turkey,
    val phoneNumber: PhoneNumber? = null,
    val profilePhotoUrl: String? = null,
    val birthDate: Long? = null,
    val isUploadingPhoto: Boolean = false
)

class ProfileViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: ProfileScreenState by mutableStateOf(ProfileScreenState())
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
            customerRepository.readMeCustomerFlow().collectLatest {
                if (it.isSuccess()) {
                    val fetchedCustomer = it.getSuccessData()
                    screenState = ProfileScreenState(
                            id = fetchedCustomer.id,
                            firstName = fetchedCustomer.firstName,
                            lastName = fetchedCustomer.lastName,
                            email = fetchedCustomer.email,
                            city = fetchedCustomer.city,
                            postalCode = fetchedCustomer.postalCode,
                            address = fetchedCustomer.address,
                            country = Country.entries.firstOrNull {country -> country.dialCode == fetchedCustomer.phoneNumber?.dialCode }
                                ?: Country.Turkey,
                            phoneNumber = fetchedCustomer.phoneNumber,
                            profilePhotoUrl = fetchedCustomer.profilePhotoUrl,
                            birthDate = fetchedCustomer.birthDate
                        )
                    screenReady = RequestState.Success(Unit)
                } else if(it.isError()) {
                    screenReady = RequestState.Error(it.getErrorMessage())
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

    fun updateBirthDate(birthDate: Long) {
        screenState = screenState.copy(birthDate = birthDate)
        viewModelScope.launch {
            customerRepository.updateBirthDate(
                birthDate = birthDate,
                onSuccess = {},
                onError = {}
            )
        }
    }

    fun uploadProfilePhoto(
        imageBytes: ByteArray,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        screenState = screenState.copy(isUploadingPhoto = true)
        viewModelScope.launch {
            customerRepository.uploadProfilePhoto(
                imageBytes = imageBytes,
                onSuccess = { photoUrl ->
                    viewModelScope.launch {
                        customerRepository.updateProfilePhoto(
                            photoUrl = photoUrl,
                            onSuccess = {
                                screenState = screenState.copy(
                                    profilePhotoUrl = photoUrl,
                                    isUploadingPhoto = false
                                )
                                onSuccess()
                            },
                            onError = { message ->
                                screenState = screenState.copy(isUploadingPhoto = false)
                                onError(message)
                            }
                        )
                    }
                },
                onError = { message ->
                    screenState = screenState.copy(isUploadingPhoto = false)
                    onError(message)
                }
            )
        }
    }

    fun updateCustomer(
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
}