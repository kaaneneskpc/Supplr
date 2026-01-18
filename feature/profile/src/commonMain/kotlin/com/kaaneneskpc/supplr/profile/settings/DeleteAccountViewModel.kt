package com.kaaneneskpc.supplr.profile.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import kotlinx.coroutines.launch

data class DeleteAccountScreenState(
    val password: String = "",
    val isLoading: Boolean = false
)

class DeleteAccountViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    var screenState: DeleteAccountScreenState by mutableStateOf(DeleteAccountScreenState())
        private set

    val isFormValid: Boolean
        get() = screenState.password.length >= 6

    fun updatePassword(value: String) {
        screenState = screenState.copy(password = value)
    }

    fun deleteAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isFormValid) {
            onError("Please enter your password")
            return
        }
        screenState = screenState.copy(isLoading = true)
        viewModelScope.launch {
            customerRepository.deleteAccount(
                password = screenState.password,
                onSuccess = {
                    screenState = screenState.copy(isLoading = false)
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
