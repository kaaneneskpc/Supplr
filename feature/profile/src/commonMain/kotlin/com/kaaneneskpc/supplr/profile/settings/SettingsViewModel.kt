package com.kaaneneskpc.supplr.profile.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.shared.domain.CommunicationPreferences
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SettingsScreenState(
    val isEmailEnabled: Boolean = true,
    val isPushEnabled: Boolean = true,
    val isSmsEnabled: Boolean = false,
    val isTwoFactorEnabled: Boolean = false,
    val isLoading: Boolean = false
)

class SettingsViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    var screenState: SettingsScreenState by mutableStateOf(SettingsScreenState())
        private set
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
        private set

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            customerRepository.readMeCustomerFlow().collectLatest { result ->
                if (result.isSuccess()) {
                    val customer = result.getSuccessData()
                    screenState = SettingsScreenState(
                        isEmailEnabled = customer.communicationPreferences?.isEmailEnabled ?: true,
                        isPushEnabled = customer.communicationPreferences?.isPushEnabled ?: true,
                        isSmsEnabled = customer.communicationPreferences?.isSmsEnabled ?: false,
                        isTwoFactorEnabled = customer.isTwoFactorEnabled
                    )
                    screenReady = RequestState.Success(Unit)
                } else if (result.isError()) {
                    screenReady = RequestState.Error(result.getErrorMessage())
                }
            }
        }
    }

    fun updateEmailPreference(isEnabled: Boolean) {
        screenState = screenState.copy(isEmailEnabled = isEnabled)
        saveCommunicationPreferences()
    }

    fun updatePushPreference(isEnabled: Boolean) {
        screenState = screenState.copy(isPushEnabled = isEnabled)
        saveCommunicationPreferences()
    }

    fun updateSmsPreference(isEnabled: Boolean) {
        screenState = screenState.copy(isSmsEnabled = isEnabled)
        saveCommunicationPreferences()
    }

    private fun saveCommunicationPreferences() {
        viewModelScope.launch {
            val preferences = CommunicationPreferences(
                isEmailEnabled = screenState.isEmailEnabled,
                isPushEnabled = screenState.isPushEnabled,
                isSmsEnabled = screenState.isSmsEnabled
            )
            customerRepository.updateCommunicationPreferences(
                preferences = preferences,
                onSuccess = {},
                onError = {}
            )
        }
    }
}
