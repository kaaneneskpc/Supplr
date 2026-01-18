package com.kaaneneskpc.supplr.profile.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import kotlinx.coroutines.launch

data class ChangePasswordScreenState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
)

class ChangePasswordViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    var screenState: ChangePasswordScreenState by mutableStateOf(ChangePasswordScreenState())
        private set

    val isFormValid: Boolean
        get() = with(screenState) {
            currentPassword.length >= 6 &&
            newPassword.length >= 6 &&
            newPassword == confirmPassword
        }

    fun updateCurrentPassword(value: String) {
        screenState = screenState.copy(currentPassword = value)
    }

    fun updateNewPassword(value: String) {
        screenState = screenState.copy(newPassword = value)
    }

    fun updateConfirmPassword(value: String) {
        screenState = screenState.copy(confirmPassword = value)
    }

    fun changePassword(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isFormValid) {
            onError("Please fill in all fields correctly")
            return
        }
        screenState = screenState.copy(isLoading = true)
        viewModelScope.launch {
            customerRepository.changePassword(
                currentPassword = screenState.currentPassword,
                newPassword = screenState.newPassword,
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
