package com.kaaneneskpc.supplr.gamification.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.GamificationRepository
import com.kaaneneskpc.supplr.shared.domain.gamification.LeaderboardEntry
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {
    private val _leaderboard = MutableStateFlow<RequestState<List<LeaderboardEntry>>>(RequestState.Loading)
    val leaderboard: StateFlow<RequestState<List<LeaderboardEntry>>> = _leaderboard.asStateFlow()

    private val _userRank = MutableStateFlow<RequestState<LeaderboardEntry?>>(RequestState.Idle)
    val userRank: StateFlow<RequestState<LeaderboardEntry?>> = _userRank.asStateFlow()

    init {
        loadLeaderboard()
        loadUserRank()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            _leaderboard.value = RequestState.Loading
            gamificationRepository.getLeaderboard().collect { result ->
                _leaderboard.value = result
            }
        }
    }

    private fun loadUserRank() {
        val userId = gamificationRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            gamificationRepository.getUserRank(userId).collect { state ->
                _userRank.value = state
            }
        }
    }

    fun refreshLeaderboard() {
        loadLeaderboard()
        loadUserRank()
    }
}
