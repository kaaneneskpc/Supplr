package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.AdminRepository
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.withTimeout
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AdminRepositoryImpl : AdminRepository {
    override fun getCurrentUserId(): String? = Firebase.auth.currentUser?.uid

    override suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                val fireStore = Firebase.firestore
                val productCollection = fireStore.collection("product")
                productCollection.document(product.id).set(product)
                onSuccess()
            } else {
                onError("User is not available")
            }
        } catch (e: Exception) {
            onError("An error occurred while creating the product : ${e.message}")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun uploadImageToStorage(file: File): String? {
        return if (getCurrentUserId() != null) {
            val storage = Firebase.storage.reference
            val imagePath = storage.child("images/${Uuid.random().toHexString()}")
            try {
                withTimeout(timeMillis = 20000) {
                    imagePath.putFile(file)
                    imagePath.getDownloadUrl()
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    override suspend fun deleteImageFromStorage(
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val storagePath = extractFirebaseStoragePath(downloadUrl)
            if (storagePath != null) {
                Firebase.storage.reference(storagePath).delete()
                onSuccess()
            } else {
                onError("Storage Path is null.")
            }
        } catch (e: Exception) {
            onError("Error while deleting a thumbnail: $e")
        }
    }

    override suspend fun readProductById(id: String): RequestState<Product> {
        return try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productDocument = database.collection(collectionPath = "product")
                    .document(id)
                    .get()
                if (productDocument.exists) {
                    val product = Product(
                        id = productDocument.id,
                        title = productDocument.get(field = "title"),
                        createdAt = productDocument.get(field = "createdAt"),
                        description = productDocument.get(field = "description"),
                        thumbnail = productDocument.get(field = "thumbnail"),
                        category = productDocument.get(field = "category"),
                        flavors = productDocument.get(field = "flavors"),
                        weight = productDocument.get(field = "weight"),
                        price = productDocument.get(field = "price"),
                        isPopular = productDocument.get(field = "isPopular"),
                        isDiscounted = productDocument.get(field = "isDiscounted"),
                        isNew = productDocument.get(field = "isNew")
                    )
                    RequestState.Success(product.copy(title = product.title.uppercase()))
                } else {
                    RequestState.Error("Selected product not found.")
                }
            } else {
                RequestState.Error("User is not available.")
            }
        } catch (e: Exception) {
            RequestState.Error("Error while reading a selected product: ${e.message}")
        }
    }

    override suspend fun updateProductThumbnail(
        productId: String,
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productCollection = database.collection(collectionPath = "product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()
                if (existingProduct.exists) {
                    productCollection.document(productId)
                        .update("thumbnail" to downloadUrl)
                    onSuccess()
                } else {
                    onError("Selected Product not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating a thumbnail image: ${e.message}")
        }
    }

    private fun extractFirebaseStoragePath(downloadUrl: String): String? {
        val startIndex = downloadUrl.indexOf("/o/") + 3
        if (startIndex < 3) return null

        val endIndex = downloadUrl.indexOf("?", startIndex)
        val encodedPath = if (endIndex != -1) {
            downloadUrl.substring(startIndex, endIndex)
        } else {
            downloadUrl.substring(startIndex)
        }

        return decodeFirebasePath(encodedPath)
    }

    private fun decodeFirebasePath(encodedPath: String): String {
        return encodedPath
            .replace("%2F", "/")
            .replace("%20", " ")
    }


}