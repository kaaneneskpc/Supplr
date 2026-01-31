package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.ProductRepository
import com.kaaneneskpc.supplr.shared.domain.PaginatedResult
import com.kaaneneskpc.supplr.shared.domain.Product
import com.kaaneneskpc.supplr.shared.domain.ProductCategory
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class ProductRepositoryImpl : ProductRepository {
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid
    override fun readDiscountedProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection(collectionPath = "product")
                    .where { "isDiscounted" equalTo true }
                    .snapshots
                    .collectLatest { query ->
                        val products = query.documents.map { document ->
                            Product(
                                id = document.id,
                                title = document.get(field = "title"),
                                createdAt = document.get(field = "createdAt"),
                                description = document.get(field = "description"),
                                thumbnail = document.get(field = "thumbnail"),
                                category = document.get(field = "category"),
                                flavors = document.get(field = "flavors"),
                                weight = document.get(field = "weight"),
                                price = document.get(field = "price"),
                                isPopular = document.get(field = "isPopular"),
                                isDiscounted = document.get(field = "isDiscounted"),
                                isNew = document.get(field = "isNew")
                            )
                        }
                        send(RequestState.Success(data = products.map { it.copy(title = it.title.uppercase()) }))
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading the new or discounted items from the database: ${e.message}"))
        }
    }

    override fun readNewProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection(collectionPath = "product")
                    .where { "isNew" equalTo true }
                    .snapshots
                    .collectLatest { query ->
                        val products = query.documents.map { document ->
                            Product(
                                id = document.id,
                                title = document.get(field = "title"),
                                createdAt = document.get(field = "createdAt"),
                                description = document.get(field = "description"),
                                thumbnail = document.get(field = "thumbnail"),
                                category = document.get(field = "category"),
                                flavors = document.get(field = "flavors"),
                                weight = document.get(field = "weight"),
                                price = document.get(field = "price"),
                                isPopular = document.get(field = "isPopular"),
                                isDiscounted = document.get(field = "isDiscounted"),
                                isNew = document.get(field = "isNew")
                            )
                        }
                        send(RequestState.Success(data = products.map { it.copy(title = it.title.uppercase()) }))
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading the new or discounted items from the database: ${e.message}"))
        }
    }

    override fun readProductByIdFlow(id: String): Flow<RequestState<Product>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection(collectionPath = "product")
                    .document(id)
                    .snapshots
                    .collectLatest { document ->
                        if (document.exists) {
                            val product = Product(
                                id = document.id,
                                title = document.get(field = "title"),
                                createdAt = document.get(field = "createdAt"),
                                description = document.get(field = "description"),
                                thumbnail = document.get(field = "thumbnail"),
                                category = document.get(field = "category"),
                                flavors = document.get(field = "flavors"),
                                weight = document.get(field = "weight"),
                                price = document.get(field = "price"),
                                isPopular = document.get(field = "isPopular"),
                                isDiscounted = document.get(field = "isDiscounted"),
                                isNew = document.get(field = "isNew")
                            )
                            send(RequestState.Success(product.copy(title = product.title.uppercase())))
                        } else {
                            send(RequestState.Error("Selected product does not exist."))
                        }
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading a selected product: ${e.message}"))
        }
    }

    override fun readProductsByIdsFlow(ids: List<String>): Flow<RequestState<List<Product>>> =
        channelFlow {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    val database = Firebase.firestore
                    val productCollection = database.collection(collectionPath = "product")

                    val allProducts = mutableListOf<Product>()
                    val chunks = ids.chunked(10)

                    chunks.forEachIndexed { index, chunk ->
                        productCollection
                            .where { "id" inArray chunk }
                            .snapshots
                            .collectLatest { query ->
                                val products = query.documents.map { document ->
                                    Product(
                                        id = document.id,
                                        title = document.get(field = "title"),
                                        createdAt = document.get(field = "createdAt"),
                                        description = document.get(field = "description"),
                                        thumbnail = document.get(field = "thumbnail"),
                                        category = document.get(field = "category"),
                                        flavors = document.get(field = "flavors"),
                                        weight = document.get(field = "weight"),
                                        price = document.get(field = "price"),
                                        isPopular = document.get(field = "isPopular"),
                                        isDiscounted = document.get(field = "isDiscounted"),
                                        isNew = document.get(field = "isNew")
                                    )
                                }
                                allProducts.addAll(products.map { it.copy(title = it.title.uppercase()) })

                                if (index == chunks.lastIndex) {
                                    send(RequestState.Success(allProducts))
                                }
                            }
                    }
                } else {
                    send(RequestState.Error("User is not available."))
                }
            } catch (e: Exception) {
                send(RequestState.Error("Error while reading a selected product: ${e.message}"))
            }
        }

    override fun readProductsByCategoryFlow(category: ProductCategory): Flow<RequestState<List<Product>>> =
        channelFlow {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    val database = Firebase.firestore
                    database.collection(collectionPath = "product")
                        .where { "category" equalTo category.name }
                        .snapshots
                        .collectLatest { query ->
                            val products = query.documents.map { document ->
                                Product(
                                    id = document.id,
                                    title = document.get(field = "title"),
                                    createdAt = document.get(field = "createdAt"),
                                    description = document.get(field = "description"),
                                    thumbnail = document.get(field = "thumbnail"),
                                    category = document.get(field = "category"),
                                    flavors = document.get(field = "flavors"),
                                    weight = document.get(field = "weight"),
                                    price = document.get(field = "price"),
                                    isPopular = document.get(field = "isPopular"),
                                    isDiscounted = document.get(field = "isDiscounted"),
                                    isNew = document.get(field = "isNew")
                                )
                            }
                            send(RequestState.Success(products.map { it.copy(title = it.title.uppercase()) }))
                        }
                } else {
                    send(RequestState.Error("User is not available."))
                }
            } catch (e: Exception) {
                send(RequestState.Error("Error while reading a selected product: ${e.message}"))
            }
        }

    override suspend fun readProductsByCategoryPaginated(
        category: ProductCategory,
        pageSize: Int,
        lastDocumentId: String?
    ): RequestState<PaginatedResult<Product>> {
        return try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                val productCollection = database.collection(collectionPath = "product")
                var query = productCollection
                    .where { "category" equalTo category.name }
                    .orderBy("createdAt", Direction.DESCENDING)
                    .limit(pageSize + 1)
                if (lastDocumentId != null) {
                    val lastDocument = productCollection.document(lastDocumentId).get()
                    query = productCollection
                        .where { "category" equalTo category.name }
                        .orderBy("createdAt", Direction.DESCENDING)
                        .startAfter(lastDocument)
                        .limit(pageSize + 1)
                }
                val querySnapshot = query.get()
                val documents = querySnapshot.documents
                val hasNextPage = documents.size > pageSize
                val resultDocuments = if (hasNextPage) documents.dropLast(1) else documents
                val products = resultDocuments.map { document ->
                    Product(
                        id = document.id,
                        title = document.get<String>(field = "title").uppercase(),
                        createdAt = document.get(field = "createdAt"),
                        description = document.get(field = "description"),
                        thumbnail = document.get(field = "thumbnail"),
                        category = document.get(field = "category"),
                        flavors = document.get(field = "flavors"),
                        weight = document.get(field = "weight"),
                        price = document.get(field = "price"),
                        isPopular = document.get(field = "isPopular"),
                        isDiscounted = document.get(field = "isDiscounted"),
                        isNew = document.get(field = "isNew")
                    )
                }
                val newLastDocumentId = resultDocuments.lastOrNull()?.id
                RequestState.Success(
                    PaginatedResult(
                        items = products,
                        lastDocumentId = newLastDocumentId,
                        hasNextPage = hasNextPage
                    )
                )
            } else {
                RequestState.Error("User is not available.")
            }
        } catch (e: Exception) {
            RequestState.Error("Error while reading paginated products: ${e.message}")
        }
    }
}