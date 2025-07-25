package com.kaaneneskpc.supplr.data.domain

import com.kaaneneskpc.supplr.shared.domain.Location
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentUserId(): String?
    
    /**
     * Fetches all locations for a specific user
     */
    fun getLocationsForUser(userId: String): Flow<RequestState<List<Location>>>
    
    /**
     * Adds a new location for the user
     */
    suspend fun addLocation(
        title: String,
        category: com.kaaneneskpc.supplr.shared.domain.LocationCategory,
        fullAddress: String,
        city: String,
        state: String?,
        postalCode: String,
        country: String,
        isDefault: Boolean = false,
        latitude: Double? = null,
        longitude: Double? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Updates an existing location
     */
    suspend fun updateLocation(
        location: Location,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Deletes a location
     */
    suspend fun deleteLocation(
        locationId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Sets a location as the default address
     */
    suspend fun setDefaultLocation(
        locationId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    
    /**
     * Gets the default location for a user
     */
    suspend fun getDefaultLocation(userId: String): RequestState<Location?>
} 