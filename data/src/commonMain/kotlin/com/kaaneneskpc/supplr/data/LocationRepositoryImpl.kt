package com.kaaneneskpc.supplr.data

import com.kaaneneskpc.supplr.data.domain.LocationRepository
import com.kaaneneskpc.supplr.shared.domain.Location
import com.kaaneneskpc.supplr.shared.domain.LocationCategory
import com.kaaneneskpc.supplr.shared.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class LocationRepositoryImpl : LocationRepository {
    
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override fun getLocationsForUser(userId: String): Flow<RequestState<List<Location>>> = channelFlow {
        try {
            send(RequestState.Loading)
            val database = Firebase.firestore
            database.collection("locations")
                .where { "userId" equalTo userId }
                .snapshots
                .collectLatest { query ->
                    val locations = query.documents.map { document ->
                        Location(
                            id = document.id,
                            userId = document.get("userId"),
                            title = document.get("title"),
                            category = try {
                                LocationCategory.valueOf(document.get("category") ?: "OTHER")
                            } catch (e: Exception) {
                                LocationCategory.OTHER
                            },
                            fullAddress = document.get("fullAddress"),
                            city = document.get("city"),
                            state = document.get("state"),
                            postalCode = document.get("postalCode"),
                            country = document.get("country"),
                            isDefault = document.get("isDefault") ?: false,
                            createdAt = document.get("createdAt"),
                            latitude = document.get("latitude"),
                            longitude = document.get("longitude")
                        )
                    }
                    // Sort by creation date (newest first) and default address first
                    val sortedLocations = locations
                        .sortedWith(compareByDescending<Location> { it.isDefault }
                            .thenByDescending { it.createdAt })
                    send(RequestState.Success(sortedLocations))
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error while fetching locations: ${e.message}"))
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun addLocation(
        title: String,
        category: LocationCategory,
        fullAddress: String,
        city: String,
        state: String?,
        postalCode: String,
        country: String,
        isDefault: Boolean,
        latitude: Double?,
        longitude: Double?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                onError("User not authenticated")
                return
            }

            val database = Firebase.firestore
            val locationId = Uuid.random().toString()
            
            // If setting as default, first unset all other default locations
            if (isDefault) {
                try {
                    val userLocations = database.collection("locations")
                        .where { "userId" equalTo currentUserId }
                        .get()
                    
                    userLocations.documents.forEach { doc ->
                        if (doc.get<Boolean>("isDefault")) {
                            database.collection("locations").document(doc.id).update(
                                mapOf("isDefault" to false)
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Log error but don't fail the operation
                    println("Warning: Could not unset previous default location: ${e.message}")
                }
            }

            val locationData = mapOf(
                "userId" to currentUserId,
                "title" to title,
                "category" to category.name,
                "fullAddress" to fullAddress,
                "city" to city,
                "state" to state,
                "postalCode" to postalCode,
                "country" to country,
                "isDefault" to isDefault,
                "createdAt" to Clock.System.now().toEpochMilliseconds(),
                "latitude" to latitude,
                "longitude" to longitude
            )

            database.collection("locations")
                .document(locationId)
                .set(locationData)

            onSuccess()
        } catch (e: Exception) {
            onError("Error while adding location: ${e.message}")
        }
    }

    override suspend fun updateLocation(
        location: Location,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val database = Firebase.firestore
            
            // If setting as default, first unset all other default locations
            if (location.isDefault) {
                try {
                    val userLocations = database.collection("locations")
                        .where { "userId" equalTo location.userId }
                        .get()
                    
                    userLocations.documents.forEach { doc ->
                        if (doc.id != location.id && doc.get<Boolean>("isDefault")) {
                            database.collection("locations").document(doc.id).update(
                                mapOf("isDefault" to false)
                            )
                        }
                    }
                } catch (e: Exception) {
                    println("Warning: Could not unset previous default location: ${e.message}")
                }
            }

            val locationData = mapOf(
                "userId" to location.userId,
                "title" to location.title,
                "category" to location.category.name,
                "fullAddress" to location.fullAddress,
                "city" to location.city,
                "state" to location.state,
                "postalCode" to location.postalCode,
                "country" to location.country,
                "isDefault" to location.isDefault,
                "createdAt" to location.createdAt,
                "latitude" to location.latitude,
                "longitude" to location.longitude
            )

            database.collection("locations")
                .document(location.id)
                .set(locationData)

            onSuccess()
        } catch (e: Exception) {
            onError("Error while updating location: ${e.message}")
        }
    }

    override suspend fun deleteLocation(
        locationId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val database = Firebase.firestore
            database.collection("locations")
                .document(locationId)
                .delete()
            onSuccess()
        } catch (e: Exception) {
            onError("Error while deleting location: ${e.message}")
        }
    }

    override suspend fun setDefaultLocation(
        locationId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                onError("User not authenticated")
                return
            }

            val database = Firebase.firestore
            
            // First, unset all default locations for this user
            val userLocations = database.collection("locations")
                .where { "userId" equalTo currentUserId }
                .get()
            
            userLocations.documents.forEach { doc ->
                database.collection("locations").document(doc.id).update(
                    mapOf("isDefault" to false)
                )
            }
            
            // Then set the specified location as default
            database.collection("locations")
                .document(locationId)
                .update(mapOf("isDefault" to true))
            
            onSuccess()
        } catch (e: Exception) {
            onError("Error while setting default location: ${e.message}")
        }
    }

    override suspend fun getDefaultLocation(userId: String): RequestState<Location?> {
        return try {
            val database = Firebase.firestore
            val query = database.collection("locations")
                .where { "userId" equalTo userId }
                .where { "isDefault" equalTo true }
                .get()
            
            if (query.documents.isNotEmpty()) {
                val document = query.documents.first()
                val location = Location(
                    id = document.id,
                    userId = document.get("userId"),
                    title = document.get("title"),
                    category = try {
                        LocationCategory.valueOf(document.get("category") ?: "OTHER")
                    } catch (e: Exception) {
                        LocationCategory.OTHER
                    },
                    fullAddress = document.get("fullAddress"),
                    city = document.get("city"),
                    state = document.get("state"),
                    postalCode = document.get("postalCode"),
                    country = document.get("country"),
                    isDefault = document.get("isDefault") ?: false,
                    createdAt = document.get("createdAt"),
                    latitude = document.get("latitude"),
                    longitude = document.get("longitude")
                )
                RequestState.Success(location)
            } else {
                RequestState.Success(null)
            }
        } catch (e: Exception) {
            RequestState.Error("Error while fetching default location: ${e.message}")
        }
    }
} 