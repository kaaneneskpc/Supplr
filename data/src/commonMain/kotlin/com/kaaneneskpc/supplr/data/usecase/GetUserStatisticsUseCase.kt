package com.kaaneneskpc.supplr.data.usecase

import com.kaaneneskpc.supplr.data.domain.AdminRepository
import com.kaaneneskpc.supplr.data.domain.UserStats
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.datetime.Clock
import kotlin.time.ExperimentalTime

class GetUserStatisticsUseCase(
    private val adminRepository: AdminRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(): RequestState<UserStats> {
        return try {
            val usersResult = adminRepository.getAllUsers()
            
            when (usersResult) {
                is RequestState.Success -> {
                    val users = usersResult.data
                    val now = Clock.System.now().toEpochMilliseconds()
                    
                    // Zaman aralıklarını hesapla
                    val oneDayAgo = now - (24 * 60 * 60 * 1000)
                    val oneWeekAgo = now - (7 * 24 * 60 * 60 * 1000)
                    val oneMonthAgo = now - (30 * 24 * 60 * 60 * 1000)
                    
                    // Kullanıcıları createdAt tarihine göre filtrele
                    val newUsersToday = users.count { user -> 
                        user.createdAt >= oneDayAgo 
                    }
                    
                    val newUsersThisWeek = users.count { user -> 
                        user.createdAt >= oneWeekAgo 
                    }
                    
                    val newUsersThisMonth = users.count { user -> 
                        user.createdAt >= oneMonthAgo 
                    }
                    
                    val userStats = UserStats(
                        totalUsers = users.size,
                        newUsersToday = newUsersToday,
                        newUsersThisWeek = newUsersThisWeek,
                        newUsersThisMonth = newUsersThisMonth
                    )
                    
                    RequestState.Success(userStats)
                }
                is RequestState.Error -> {
                    RequestState.Error(usersResult.message)
                }
                else -> {
                    RequestState.Loading
                }
            }
        } catch (e: Exception) {
            RequestState.Error("User statistics calculation failed: ${e.message}")
        }
    }
} 