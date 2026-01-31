package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.gamification.LeaderboardEntry
import com.kaaneneskpc.supplr.shared.domain.gamification.SpinWheelPrize
import com.kaaneneskpc.supplr.shared.domain.gamification.UserGameResult
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface GamificationRepository {
    fun getCurrentUserId(): String?
    fun getLeaderboard(): Flow<RequestState<List<LeaderboardEntry>>>
    fun getUserRank(userId: String): Flow<RequestState<LeaderboardEntry?>>
    fun getSpinWheelPrizes(): Flow<RequestState<List<SpinWheelPrize>>>
    fun canUserSpin(userId: String): Flow<RequestState<Boolean>>
    suspend fun saveGameResult(
        result: UserGameResult,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    fun getUserGameHistory(userId: String): Flow<RequestState<List<UserGameResult>>>
}
