package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.CartItem
import com.kaaneneskpc.supplr.shared.domain.CommunicationPreferences
import com.kaaneneskpc.supplr.shared.domain.Customer
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCurrentUserId(): String?
    suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )
    suspend fun signOut(): RequestState<Unit>
    fun readMeCustomerFlow(): Flow<RequestState<Customer>>
    suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun addItemToCard(
        cartItem: CartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun updateCartItemQuantity(
        id: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun deleteCartItem(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun deleteAllCartItems(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun uploadProfilePhoto(
        imageBytes: ByteArray,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )
    suspend fun updateProfilePhoto(
        photoUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun updateBirthDate(
        birthDate: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun updateCommunicationPreferences(
        preferences: CommunicationPreferences,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun deleteAccount(
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun enableTwoFactorAuth(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun disableTwoFactorAuth(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun sendTwoFactorVerificationEmail(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun addRewardPoints(
        points: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}