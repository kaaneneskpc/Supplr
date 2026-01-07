package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.CouponRepository
import com.kaaneneskpc.supplr.shared.domain.Coupon
import com.kaaneneskpc.supplr.shared.domain.CouponType
import com.kaaneneskpc.supplr.shared.domain.CouponValidationResult
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CouponRepositoryImpl : CouponRepository {
    private val database = Firebase.firestore
    private val couponCollection = database.collection("coupons")

    override suspend fun validateCoupon(code: String, orderAmount: Double): CouponValidationResult {
        val coupon = getCouponByCode(code)
            ?: return CouponValidationResult.Invalid("Invalid coupon code.")
        if (!coupon.isActive) {
            return CouponValidationResult.Invalid("This coupon is no longer active.")
        }
        if (coupon.isExpired()) {
            return CouponValidationResult.Invalid("This coupon has expired.")
        }
        if (coupon.hasReachedUsageLimit()) {
            return CouponValidationResult.Invalid("This coupon has reached its usage limit.")
        }
        if (!coupon.meetsMinimumOrderAmount(orderAmount)) {
            return CouponValidationResult.Invalid(
                "Minimum order amount of $${coupon.minimumOrderAmount} required."
            )
        }
        val discountAmount = coupon.calculateDiscount(orderAmount)
        return CouponValidationResult.Valid(coupon, discountAmount)
    }

    override suspend fun getCouponByCode(code: String): Coupon? {
        return try {
            val querySnapshot = couponCollection
                .where { "code" equalTo code.uppercase() }
                .get()
            if (querySnapshot.documents.isEmpty()) {
                null
            } else {
                val document = querySnapshot.documents.first()
                parseCouponFromDocument(document.id, document)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun incrementCouponUsage(couponId: String): Result<Unit> {
        return try {
            val couponRef = couponCollection.document(couponId)
            val couponSnapshot = couponRef.get()
            if (couponSnapshot.exists) {
                val currentUsageCount: Int = couponSnapshot.get("usageCount") ?: 0
                couponRef.update("usageCount" to (currentUsageCount + 1))
                Result.success(Unit)
            } else {
                Result.failure(Exception("Coupon not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllCoupons(): Flow<List<Coupon>> = callbackFlow {
        val listener = couponCollection.snapshots.collect { snapshot ->
            val coupons = snapshot.documents.map { document ->
                parseCouponFromDocument(document.id, document)
            }
            trySend(coupons)
        }
        awaitClose { }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun createCoupon(coupon: Coupon): Result<Unit> {
        return try {
            val couponData = mapOf(
                "code" to coupon.code.uppercase(),
                "type" to coupon.type.name,
                "value" to coupon.value,
                "minimumOrderAmount" to coupon.minimumOrderAmount,
                "maximumDiscount" to coupon.maximumDiscount,
                "usageLimit" to coupon.usageLimit,
                "usageCount" to 0,
                "expirationDate" to coupon.expirationDate,
                "isActive" to coupon.isActive,
                "createdAt" to Clock.System.now().toEpochMilliseconds()
            )
            couponCollection.add(couponData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCoupon(coupon: Coupon): Result<Unit> {
        return try {
            val couponData = mapOf(
                "code" to coupon.code.uppercase(),
                "type" to coupon.type.name,
                "value" to coupon.value,
                "minimumOrderAmount" to coupon.minimumOrderAmount,
                "maximumDiscount" to coupon.maximumDiscount,
                "usageLimit" to coupon.usageLimit,
                "expirationDate" to coupon.expirationDate,
                "isActive" to coupon.isActive
            )
            couponCollection.document(coupon.id).update(couponData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCoupon(couponId: String): Result<Unit> {
        return try {
            couponCollection.document(couponId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseCouponFromDocument(id: String, document: dev.gitlive.firebase.firestore.DocumentSnapshot): Coupon {
        return Coupon(
            id = id,
            code = document.get("code") ?: "",
            type = parseCouponType(document.get("type") ?: "PERCENTAGE"),
            value = document.get("value") ?: 0.0,
            minimumOrderAmount = document.get("minimumOrderAmount") ?: 0.0,
            maximumDiscount = document.get("maximumDiscount"),
            usageLimit = document.get("usageLimit"),
            usageCount = document.get("usageCount") ?: 0,
            expirationDate = document.get("expirationDate") ?: 0L,
            isActive = document.get("isActive") ?: true,
            createdAt = document.get("createdAt") ?: 0L
        )
    }

    private fun parseCouponType(typeString: String): CouponType {
        return when (typeString.uppercase()) {
            "PERCENTAGE" -> CouponType.PERCENTAGE
            "FIXED_AMOUNT" -> CouponType.FIXED_AMOUNT
            "FREE_SHIPPING" -> CouponType.FREE_SHIPPING
            else -> CouponType.PERCENTAGE
        }
    }
}
