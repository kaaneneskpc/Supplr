package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.CouponRepository
import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.data.domain.GamificationRepository
import com.kaaneneskpc.supplr.shared.domain.Coupon
import com.kaaneneskpc.supplr.shared.domain.CouponType
import com.kaaneneskpc.supplr.shared.domain.gamification.LeaderboardEntry
import com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType
import com.kaaneneskpc.supplr.shared.domain.gamification.SpinWheelPrize
import com.kaaneneskpc.supplr.shared.domain.gamification.UserGameResult
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class GamificationRepositoryImpl(
    private val customerRepository: CustomerRepository,
    private val couponRepository: CouponRepository
) : GamificationRepository {
    companion object {
        private const val COLLECTION_SPIN_WHEEL_PRIZES = "spin_wheel_prizes"
        private const val COLLECTION_USER_GAME_RESULTS = "user_game_results"
        private const val COLLECTION_ORDERS = "order"
        private const val LEADERBOARD_LIMIT = 10
        private const val MILLIS_IN_A_DAY = 86400000L
        private const val COUPON_VALIDITY_DAYS = 30L
    }

    override fun getCurrentUserId(): String? = Firebase.auth.currentUser?.uid

    override fun getLeaderboard(): Flow<RequestState<List<LeaderboardEntry>>> = flow {
        emit(RequestState.Success(getMockLeaderboardData()))
    }

    private fun getMockLeaderboardData(): List<LeaderboardEntry> = listOf(
        LeaderboardEntry("1", "Kaan Enes Kapici", null, 2450.00, 28, 1),
        LeaderboardEntry("2", "James Rodriguez", null, 1890.50, 22, 2),
        LeaderboardEntry("3", "Sophie Chen", null, 1650.75, 19, 3),
        LeaderboardEntry("4", "Michael Brown", null, 1420.30, 16, 4),
        LeaderboardEntry("5", "Isabella Garcia", null, 1180.00, 14, 5),
        LeaderboardEntry("6", "David Kim", null, 980.25, 12, 6),
        LeaderboardEntry("7", "Olivia Martinez", null, 850.00, 10, 7),
        LeaderboardEntry("8", "William Johnson", null, 720.50, 8, 8),
        LeaderboardEntry("9", "Ava Thompson", null, 590.75, 7, 9),
        LeaderboardEntry("10", "Lucas Anderson", null, 450.00, 5, 10)
    )

    override fun getUserRank(userId: String): Flow<RequestState<LeaderboardEntry?>> = channelFlow {
        try {
            val database = Firebase.firestore
            database.collection(COLLECTION_ORDERS)
                .where { "status" equalTo "DELIVERED" }
                .snapshots
                .collectLatest { querySnapshot ->
                    val userSpendingMap = mutableMapOf<String, Pair<Double, Int>>()
                    querySnapshot.documents.forEach { document ->
                        try {
                            val customerId: String = document.get("customerId") ?: ""
                            val totalAmount: Double = document.get("totalAmount") ?: 0.0
                            if (customerId.isNotEmpty()) {
                                val currentData = userSpendingMap[customerId] ?: Pair(0.0, 0)
                                userSpendingMap[customerId] = Pair(
                                    currentData.first + totalAmount,
                                    currentData.second + 1
                                )
                            }
                        } catch (_: Exception) { }
                    }
                    val sortedEntries = userSpendingMap.entries
                        .sortedByDescending { it.value.first }
                    val userIndex = sortedEntries.indexOfFirst { it.key == userId }
                    if (userIndex >= 0) {
                        val entry = sortedEntries[userIndex]
                        val customerDoc = database.collection("customer")
                            .document(userId)
                            .get()
                        val firstName: String = customerDoc.get("firstName") ?: ""
                        val lastName: String = customerDoc.get("lastName") ?: ""
                        val profilePhotoUrl: String? = customerDoc.get("profilePhotoUrl")
                        val userEntry = LeaderboardEntry(
                            userId = userId,
                            userName = "$firstName $lastName".trim().ifEmpty { "User" },
                            profilePhotoUrl = profilePhotoUrl,
                            totalSpent = entry.value.first,
                            totalOrders = entry.value.second,
                            rank = userIndex + 1
                        )
                        send(RequestState.Success(userEntry))
                    } else {
                        send(RequestState.Success(null))
                    }
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error fetching user rank: ${e.message}"))
        }
    }

    override fun getSpinWheelPrizes(): Flow<RequestState<List<SpinWheelPrize>>> = channelFlow {
        try {
            val database = Firebase.firestore
            database.collection(COLLECTION_SPIN_WHEEL_PRIZES)
                .snapshots
                .collectLatest { querySnapshot ->
                    val prizes = querySnapshot.documents.mapNotNull { document ->
                        try {
                            SpinWheelPrize(
                                id = document.id,
                                name = document.get("name") ?: "",
                                prizeType = PrizeType.fromString(document.get("prizeType") ?: "EMPTY"),
                                value = document.get("value") ?: 0.0,
                                couponCode = document.get("couponCode"),
                                color = document.get("color") ?: 0xFF5733L
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                    send(RequestState.Success(prizes))
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error fetching spin wheel prizes: ${e.message}"))
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun canUserSpin(userId: String): Flow<RequestState<Boolean>> = channelFlow {
        try {
            val database = Firebase.firestore
            val todayStart = Clock.System.now().toEpochMilliseconds() - MILLIS_IN_A_DAY
            database.collection(COLLECTION_USER_GAME_RESULTS)
                .where { "userId" equalTo userId }
                .snapshots
                .collectLatest { querySnapshot ->
                    val todaySpins = querySnapshot.documents.filter { document ->
                        try {
                            val createdAt: Long = document.get("createdAt") ?: 0L
                            createdAt > todayStart
                        } catch (_: Exception) {
                            false
                        }
                    }
                    send(RequestState.Success(todaySpins.isEmpty()))
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error checking spin eligibility: ${e.message}"))
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun saveGameResult(
        result: UserGameResult,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                database.collection(COLLECTION_USER_GAME_RESULTS)
                    .document(result.id)
                    .set(result)
                when {
                    shouldCreateCoupon(result.prizeType) && result.couponCode != null -> {
                        createCouponFromPrize(result)
                    }
                    result.prizeType == PrizeType.POINTS -> {
                        addPointsFromPrize(result)
                    }
                }
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error saving game result: ${e.message}")
        }
    }

    private suspend fun addPointsFromPrize(result: UserGameResult) {
        customerRepository.addRewardPoints(
            points = result.prizeValue.toInt(),
            onSuccess = { },
            onError = { }
        )
    }

    private fun shouldCreateCoupon(prizeType: PrizeType): Boolean {
        return prizeType == PrizeType.DISCOUNT_PERCENTAGE || 
               prizeType == PrizeType.DISCOUNT_FIXED ||
               prizeType == PrizeType.FREE_SHIPPING
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun createCouponFromPrize(result: UserGameResult) {
        val couponType = when (result.prizeType) {
            PrizeType.DISCOUNT_PERCENTAGE -> CouponType.PERCENTAGE
            PrizeType.DISCOUNT_FIXED -> CouponType.FIXED_AMOUNT
            PrizeType.FREE_SHIPPING -> CouponType.FREE_SHIPPING
            else -> CouponType.PERCENTAGE
        }
        val expirationDate = Clock.System.now().toEpochMilliseconds() + 
            (COUPON_VALIDITY_DAYS * MILLIS_IN_A_DAY)
        val coupon = Coupon(
            id = result.id,
            code = result.couponCode?.uppercase() ?: "",
            type = couponType,
            value = result.prizeValue,
            minimumOrderAmount = 0.0,
            maximumDiscount = null,
            usageLimit = 1,
            usageCount = 0,
            expirationDate = expirationDate,
            isActive = true,
            createdAt = result.createdAt
        )
        couponRepository.createCoupon(coupon)
    }

    override fun getUserGameHistory(userId: String): Flow<RequestState<List<UserGameResult>>> = channelFlow {
        try {
            val database = Firebase.firestore
            database.collection(COLLECTION_USER_GAME_RESULTS)
                .where { "userId" equalTo userId }
                .snapshots
                .collectLatest { querySnapshot ->
                    val results = querySnapshot.documents.mapNotNull { document ->
                        try {
                            UserGameResult(
                                id = document.id,
                                userId = document.get("userId") ?: "",
                                prizeId = document.get("prizeId") ?: "",
                                prizeName = document.get("prizeName") ?: "",
                                prizeValue = document.get("prizeValue") ?: 0.0,
                                prizeType = PrizeType.fromString(document.get("prizeType") ?: "EMPTY"),
                                couponCode = document.get("couponCode"),
                                createdAt = document.get("createdAt") ?: 0L,
                                isRedeemed = document.get<Boolean>("isRedeemed")
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }.sortedByDescending { result -> result.createdAt }
                    send(RequestState.Success(results))
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error fetching game history: ${e.message}"))
        }
    }
}
