package com.kaaneneskpc.supplr.admin_panel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.CouponRepository
import com.kaaneneskpc.supplr.shared.domain.Coupon
import com.kaaneneskpc.supplr.shared.domain.CouponType
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class CouponFormState(
    val code: String = "",
    val type: CouponType = CouponType.PERCENTAGE,
    val value: String = "",
    val minimumOrderAmount: String = "",
    val maximumDiscount: String = "",
    val usageLimit: String = "",
    val expirationDays: String = "30",
    val isActive: Boolean = true,
    val isEditing: Boolean = false,
    val editingCouponId: String? = null
)

class AdminCouponsViewModel(
    private val couponRepository: CouponRepository
) : ViewModel() {
    private val _coupons = MutableStateFlow<RequestState<List<Coupon>>>(RequestState.Loading)
    val coupons: StateFlow<RequestState<List<Coupon>>> = _coupons.asStateFlow()

    var formState by mutableStateOf(CouponFormState())
        private set
    var isFormVisible by mutableStateOf(false)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            couponRepository.getAllCoupons().collect { couponList ->
                _coupons.value = RequestState.Success(couponList)
            }
        }
    }

    fun showCreateForm() {
        formState = CouponFormState()
        isFormVisible = true
    }

    fun showEditForm(coupon: Coupon) {
        formState = CouponFormState(
            code = coupon.code,
            type = coupon.type,
            value = coupon.value.toString(),
            minimumOrderAmount = coupon.minimumOrderAmount.toString(),
            maximumDiscount = coupon.maximumDiscount?.toString() ?: "",
            usageLimit = coupon.usageLimit?.toString() ?: "",
            expirationDays = calculateRemainingDays(coupon.expirationDate).toString(),
            isActive = coupon.isActive,
            isEditing = true,
            editingCouponId = coupon.id
        )
        isFormVisible = true
    }

    fun hideForm() {
        isFormVisible = false
        formState = CouponFormState()
    }

    fun updateCode(value: String) {
        formState = formState.copy(code = value.uppercase())
    }

    fun updateType(value: CouponType) {
        formState = formState.copy(type = value)
    }

    fun updateValue(value: String) {
        formState = formState.copy(value = value)
    }

    fun updateMinimumOrderAmount(value: String) {
        formState = formState.copy(minimumOrderAmount = value)
    }

    fun updateMaximumDiscount(value: String) {
        formState = formState.copy(maximumDiscount = value)
    }

    fun updateUsageLimit(value: String) {
        formState = formState.copy(usageLimit = value)
    }

    fun updateExpirationDays(value: String) {
        formState = formState.copy(expirationDays = value)
    }

    fun updateIsActive(value: Boolean) {
        formState = formState.copy(isActive = value)
    }

    @OptIn(ExperimentalTime::class)
    fun saveCoupon() {
        if (!validateForm()) return
        isSaving = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val expirationDate = Clock.System.now().toEpochMilliseconds() +
                        (formState.expirationDays.toLongOrNull() ?: 30) * 24 * 60 * 60 * 1000
                val coupon = Coupon(
                    id = formState.editingCouponId ?: "",
                    code = formState.code.uppercase(),
                    type = formState.type,
                    value = formState.value.toDoubleOrNull() ?: 0.0,
                    minimumOrderAmount = formState.minimumOrderAmount.toDoubleOrNull() ?: 0.0,
                    maximumDiscount = formState.maximumDiscount.toDoubleOrNull(),
                    usageLimit = formState.usageLimit.toIntOrNull(),
                    expirationDate = expirationDate,
                    isActive = formState.isActive
                )
                val result = if (formState.isEditing) {
                    couponRepository.updateCoupon(coupon)
                } else {
                    couponRepository.createCoupon(coupon)
                }
                result.fold(
                    onSuccess = {
                        successMessage = if (formState.isEditing) "Coupon updated!" else "Coupon created!"
                        hideForm()
                    },
                    onFailure = { error ->
                        errorMessage = error.message ?: "Failed to save coupon"
                    }
                )
            } catch (e: Exception) {
                errorMessage = e.message ?: "An error occurred"
            } finally {
                isSaving = false
            }
        }
    }

    fun deleteCoupon(couponId: String) {
        viewModelScope.launch {
            val result = couponRepository.deleteCoupon(couponId)
            result.fold(
                onSuccess = {
                    successMessage = "Coupon deleted!"
                },
                onFailure = { error ->
                    errorMessage = error.message ?: "Failed to delete coupon"
                }
            )
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }

    private fun validateForm(): Boolean {
        if (formState.code.isBlank()) {
            errorMessage = "Please enter a coupon code"
            return false
        }
        if (formState.value.isBlank() || formState.value.toDoubleOrNull() == null) {
            errorMessage = "Please enter a valid discount value"
            return false
        }
        if (formState.type == CouponType.PERCENTAGE) {
            val value = formState.value.toDoubleOrNull() ?: 0.0
            if (value <= 0 || value > 100) {
                errorMessage = "Percentage must be between 1 and 100"
                return false
            }
        }
        return true
    }

    @OptIn(ExperimentalTime::class)
    private fun calculateRemainingDays(expirationDate: Long): Int {
        val now = Clock.System.now().toEpochMilliseconds()
        val remainingMillis = expirationDate - now
        return (remainingMillis / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(1)
    }
}
