package com.kaaneneskpc.supplr.profile.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class TwoFactorAuthScreenState(
    val isTwoFactorEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isEmailSent: Boolean = false
)

class TwoFactorAuthViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    var screenState: TwoFactorAuthScreenState by mutableStateOf(TwoFactorAuthScreenState())
        private set
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
        private set

    init {
        loadTwoFactorStatus()
    }

    private fun loadTwoFactorStatus() {
        viewModelScope.launch {
            customerRepository.readMeCustomerFlow().collectLatest { result ->
                if (result.isSuccess()) {
                    val customer = result.getSuccessData()
                    screenState = screenState.copy(isTwoFactorEnabled = customer.isTwoFactorEnabled)
                    screenReady = RequestState.Success(Unit)
                } else if (result.isError()) {
                    screenReady = RequestState.Error(result.getErrorMessage())
                }
            }
        }
    }

    fun enableTwoFactorAuth(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        screenState = screenState.copy(isLoading = true)
        viewModelScope.launch {
            customerRepository.sendTwoFactorVerificationEmail(
                onSuccess = {
                    viewModelScope.launch {
                        customerRepository.enableTwoFactorAuth(
                            onSuccess = {
                                screenState = screenState.copy(
                                    isLoading = false,
                                    isTwoFactorEnabled = true,
                                    isEmailSent = true
                                )
                                onSuccess()
                            },
                            onError = { message ->
                                screenState = screenState.copy(isLoading = false)
                                onError(message)
                            }
                        )
                    }
                },
                onError = { message ->
                    screenState = screenState.copy(isLoading = false)
                    onError(message)
                }
            )
        }
    }

    fun disableTwoFactorAuth(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        screenState = screenState.copy(isLoading = true)
        viewModelScope.launch {
            customerRepository.disableTwoFactorAuth(
                onSuccess = {
                    screenState = screenState.copy(
                        isLoading = false,
                        isTwoFactorEnabled = false
                    )
                    onSuccess()
                },
                onError = { message ->
                    screenState = screenState.copy(isLoading = false)
                    onError(message)
                }
            )
        }
    }
}
