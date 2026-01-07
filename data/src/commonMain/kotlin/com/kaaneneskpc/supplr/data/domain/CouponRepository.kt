package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Coupon
import com.kaaneneskpc.supplr.shared.domain.CouponValidationResult
import kotlinx.coroutines.flow.Flow

interface CouponRepository {
    suspend fun validateCoupon(code: String, orderAmount: Double): CouponValidationResult
    suspend fun getCouponByCode(code: String): Coupon?
    suspend fun incrementCouponUsage(couponId: String): Result<Unit>
    fun getAllCoupons(): Flow<List<Coupon>>
    suspend fun createCoupon(coupon: Coupon): Result<Unit>
    suspend fun updateCoupon(coupon: Coupon): Result<Unit>
    suspend fun deleteCoupon(couponId: String): Result<Unit>
}
