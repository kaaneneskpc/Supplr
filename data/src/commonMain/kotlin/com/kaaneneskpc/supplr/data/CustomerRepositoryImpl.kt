package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.CustomerRepository
import com.kaaneneskpc.supplr.shared.domain.CartItem
import com.kaaneneskpc.supplr.shared.domain.CommunicationPreferences
import com.kaaneneskpc.supplr.shared.domain.Customer
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class CustomerRepositoryImpl : CustomerRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            user?.let {
                val customerCollection = Firebase.firestore.collection("customer")
                val customer = Customer(
                    id = user.uid,
                    firstName = user.displayName?.split(" ")?.firstOrNull() ?: "Unknown",
                    lastName = user.displayName?.split(" ")?.lastOrNull() ?: "Unknown",
                    email = user.email ?: "Unknown",
                )
                val customerExists = customerCollection.document(user.uid).get().exists
                if (customerExists) {
                    onSuccess()
                } else {
                    customerCollection.document(user.uid).set(customer)
                    customerCollection.document(user.uid)
                        .collection("privateData")
                        .document("role")
                        .set(mapOf("isAdmin" to false))
                    onSuccess()
                }
            } ?: run {
                onError("User is not authenticated.")
            }
        } catch (e: Exception) {
            onError(e.message ?: "An error occurred while creating the customer.")
        }
    }

    override suspend fun signOut(): RequestState<Unit> {
        return try {
            Firebase.auth.signOut()
            RequestState.Success(data = Unit)
        } catch (e: Exception) {
            RequestState.Error(e.message ?: "An error occurred while signing out.")
        }
    }

    override fun readMeCustomerFlow(): Flow<RequestState<Customer>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection(collectionPath = "customer")
                    .document(userId).snapshots.collectLatest { document ->
                    if (document.exists) {
                        val privateDataDocument = database.collection(collectionPath = "customer")
                            .document(userId)
                            .collection("privateData")
                            .document("role")
                            .get()
                        val customer = Customer(
                            id = document.id,
                            firstName = document.get("firstName"),
                            lastName = document.get("lastName"),
                            email = document.get("email"),
                            createdAt = document.get("createdAt") ?: 0L,
                            city = document.get("city"),
                            postalCode = document.get("postalCode"),
                            address = document.get("address"),
                            phoneNumber = document.get("phoneNumber"),
                            cart = document.get("cart"),
                            isAdmin = privateDataDocument.get("isAdmin"),
                            profilePhotoUrl = document.get("profilePhotoUrl"),
                            birthDate = document.get("birthDate"),
                            communicationPreferences = document.get("communicationPreferences"),
                            isTwoFactorEnabled = document.get("isTwoFactorEnabled") ?: false,
                            rewardPoints = document.get("rewardPoints") ?: 0
                        )
                        send(RequestState.Success(data = customer))
                    } else {
                        send(RequestState.Error("Customer document does not exist."))
                    }
                }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error fetching customer data: ${e.message}"))
        }
    }

    override suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                val customerCollection = firestore.collection(collectionPath = "customer")
                val existingCustomer = customerCollection
                    .document(userId)
                    .get()
                if (existingCustomer.exists) {
                    customerCollection
                        .document(userId)
                        .update(
                            "firstName" to customer.firstName,
                            "lastName" to customer.lastName,
                            "city" to customer.city,
                            "postalCode" to customer.postalCode,
                            "address" to customer.address,
                            "phoneNumber" to customer.phoneNumber
                        )
                    onSuccess()
                } else {
                    onError("Customer not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a Customer information: ${e.message}")
        }
    }

    override suspend fun addItemToCard(
        cartItem: CartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val customerCollection = database.collection(collectionPath = "customer")
                val existingCustomer = customerCollection
                    .document(currentUserId)
                    .get()
                if (existingCustomer.exists) {
                    val existingCart = existingCustomer.get<List<CartItem>>("cart")
                    val updatedCart = existingCart + cartItem
                    customerCollection.document(currentUserId)
                        .set(
                            data = mapOf("cart" to updatedCart),
                            merge = true
                        )
                    onSuccess()
                } else {
                    onError("Select customer does not exist.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while adding a product to cart: ${e.message}")
        }
    }

    override suspend fun updateCartItemQuantity(
        id: String,
        quantity: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val customerCollection = database.collection(collectionPath = "customer")
                val existingCustomer = customerCollection
                    .document(currentUserId)
                    .get()
                if (existingCustomer.exists) {
                    val existingCart = existingCustomer.get<List<CartItem>>("cart")
                    val updatedCart = existingCart.map { cartItem ->
                        if (cartItem.id == id) {
                            cartItem.copy(quantity = quantity)
                        } else cartItem
                    }
                    customerCollection.document(currentUserId)
                        .update(data = mapOf("cart" to updatedCart))
                    onSuccess()
                } else {
                    onError("Select customer does not exist.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a product to cart: ${e.message}")
        }
    }

    override suspend fun deleteCartItem(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val customerCollection = database.collection(collectionPath = "customer")
                val existingCustomer = customerCollection
                    .document(currentUserId)
                    .get()
                if (existingCustomer.exists) {
                    val existingCart = existingCustomer.get<List<CartItem>>("cart")
                    val updatedCart = existingCart.filterNot { it.id == id }
                    customerCollection.document(currentUserId)
                        .update(data = mapOf("cart" to updatedCart))
                    onSuccess()
                } else {
                    onError("Select customer does not exist.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while deleting a product from cart: ${e.message}")
        }
    }

    override suspend fun deleteAllCartItems(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val database = Firebase.firestore
                val customerCollection = database.collection(collectionPath = "customer")
                val existingCustomer = customerCollection
                    .document(currentUserId)
                    .get()
                if (existingCustomer.exists) {
                    customerCollection.document(currentUserId)
                        .update(data = mapOf("cart" to emptyList<List<CartItem>>()))
                    onSuccess()
                } else {
                    onError("Select customer does not exist.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while deleting all products from cart: ${e.message}")
        }
    }

    override suspend fun uploadProfilePhoto(
        imageBytes: ByteArray,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        onError("Profile photo upload requires platform-specific implementation. Please update your profile photo URL directly.")
    }

    override suspend fun updateProfilePhoto(
        photoUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                firestore.collection("customer")
                    .document(userId)
                    .update("profilePhotoUrl" to photoUrl)
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error updating profile photo: ${e.message}")
        }
    }

    override suspend fun updateBirthDate(
        birthDate: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                firestore.collection("customer")
                    .document(userId)
                    .update("birthDate" to birthDate)
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error updating birth date: ${e.message}")
        }
    }

    override suspend fun updateCommunicationPreferences(
        preferences: CommunicationPreferences,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                firestore.collection("customer")
                    .document(userId)
                    .update("communicationPreferences" to preferences)
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error updating communication preferences: ${e.message}")
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val user = Firebase.auth.currentUser
            if (user != null && user.email != null) {
                val credential = EmailAuthProvider.credential(user.email!!, currentPassword)
                user.reauthenticate(credential)
                user.updatePassword(newPassword)
                onSuccess()
            } else {
                onError("User is not authenticated.")
            }
        } catch (e: Exception) {
            onError("Error changing password: ${e.message}")
        }
    }

    override suspend fun deleteAccount(
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val user = Firebase.auth.currentUser
            if (user != null && user.email != null) {
                val credential = EmailAuthProvider.credential(user.email!!, password)
                user.reauthenticate(credential)
                val userId = user.uid
                val firestore = Firebase.firestore
                firestore.collection("customer").document(userId).delete()
                user.delete()
                onSuccess()
            } else {
                onError("User is not authenticated.")
            }
        } catch (e: Exception) {
            onError("Error deleting account: ${e.message}")
        }
    }

    override suspend fun enableTwoFactorAuth(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                firestore.collection("customer")
                    .document(userId)
                    .update("isTwoFactorEnabled" to true)
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error enabling two-factor authentication: ${e.message}")
        }
    }

    override suspend fun disableTwoFactorAuth(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                firestore.collection("customer")
                    .document(userId)
                    .update("isTwoFactorEnabled" to false)
                onSuccess()
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error disabling two-factor authentication: ${e.message}")
        }
    }

    override suspend fun sendTwoFactorVerificationEmail(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val user = Firebase.auth.currentUser
            if (user != null) {
                user.sendEmailVerification()
                onSuccess()
            } else {
                onError("User is not authenticated.")
            }
        } catch (e: Exception) {
            onError("Error sending verification email: ${e.message}")
        }
    }

    override suspend fun addRewardPoints(
        points: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                val customerDoc = firestore.collection("customer")
                    .document(userId)
                    .get()
                if (customerDoc.exists) {
                    val currentPoints: Int = customerDoc.get("rewardPoints") ?: 0
                    firestore.collection("customer")
                        .document(userId)
                        .update("rewardPoints" to (currentPoints + points))
                    onSuccess()
                } else {
                    onError("Customer not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error adding reward points: ${e.message}")
        }
    }
}