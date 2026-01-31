package com.kaaneneskpc.supplr.gamification.spin_wheel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.GamificationRepository
import com.kaaneneskpc.supplr.shared.domain.gamification.SpinWheelPrize
import com.kaaneneskpc.supplr.shared.domain.gamification.UserGameResult
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SpinState {
    data object Idle : SpinState()
    data object Spinning : SpinState()
    data class Result(val prize: SpinWheelPrize) : SpinState()
}

class SpinWheelViewModel(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {
    val prizes = gamificationRepository.getSpinWheelPrizes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

    private val _canSpin = MutableStateFlow<RequestState<Boolean>>(RequestState.Idle)
    val canSpin: StateFlow<RequestState<Boolean>> = _canSpin.asStateFlow()

    private val _spinState = MutableStateFlow<SpinState>(SpinState.Idle)
    val spinState: StateFlow<SpinState> = _spinState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        checkSpinEligibility()
    }

    private fun checkSpinEligibility() {
        val userId = gamificationRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            gamificationRepository.canUserSpin(userId).collect { state ->
                _canSpin.value = state
            }
        }
    }

    fun startSpin(prizeIndex: Int, prizes: List<SpinWheelPrize>) {
        if (prizes.isEmpty()) return
        _spinState.value = SpinState.Spinning
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            val wonPrize = prizes[prizeIndex % prizes.size]
            _spinState.value = SpinState.Result(wonPrize)
            saveResult(wonPrize)
        }
    }

    private fun saveResult(prize: SpinWheelPrize) {
        val userId = gamificationRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            val result = UserGameResult(
                userId = userId,
                prizeId = prize.id,
                prizeName = prize.name,
                prizeValue = prize.value,
                prizeType = prize.prizeType,
                couponCode = prize.couponCode
            )
            gamificationRepository.saveGameResult(
                result = result,
                onSuccess = {
                    _message.value = "Congratulations! You won: ${prize.name}"
                    checkSpinEligibility()
                },
                onError = { error ->
                    _message.value = error
                }
            )
        }
    }

    fun resetSpin() {
        _spinState.value = SpinState.Idle
    }

    fun clearMessage() {
        _message.value = null
    }
}
