package com.kaaneneskpc.supplr.locations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.LocationRepository
import com.kaaneneskpc.supplr.shared.domain.Location
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LocationsScreenState(
    val locations: RequestState<List<Location>> = RequestState.Loading,
    val isAddingLocation: Boolean = false,
    val isUpdatingLocation: Boolean = false,
    val isDeletingLocation: Boolean = false,
    val selectedLocationForDelete: Location? = null,
    val showDeleteConfirmDialog: Boolean = false
)

data class AddEditLocationState(
    val isEditMode: Boolean = false,
    val locationId: String? = null,
    val title: String = "",
    val fullAddress: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = "",
    val isDefault: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)

class LocationsViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    var screenState: LocationsScreenState by mutableStateOf(LocationsScreenState())
        private set

    var addEditState: AddEditLocationState by mutableStateOf(AddEditLocationState())
        private set

    val isAddEditFormValid: Boolean
        get() = with(addEditState) {
            title.isNotBlank() &&
            fullAddress.isNotBlank() &&
            city.isNotBlank() &&
            postalCode.isNotBlank() &&
            country.isNotBlank()
        }

    init {
        loadUserLocations()
    }

    private fun loadUserLocations() {
        val currentUserId = locationRepository.getCurrentUserId()
        if (currentUserId != null) {
            viewModelScope.launch {
                locationRepository.getLocationsForUser(currentUserId).collectLatest { locationsState ->
                    screenState = screenState.copy(locations = locationsState)
                }
            }
        } else {
            screenState = screenState.copy(
                locations = RequestState.Error("User not authenticated")
            )
        }
    }

    fun addLocation(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            screenState = screenState.copy(isAddingLocation = true)
            
            locationRepository.addLocation(
                title = addEditState.title,
                fullAddress = addEditState.fullAddress,
                city = addEditState.city,
                state = addEditState.state.takeIf { it.isNotBlank() },
                postalCode = addEditState.postalCode,
                country = addEditState.country,
                isDefault = addEditState.isDefault,
                latitude = addEditState.latitude,
                longitude = addEditState.longitude,
                onSuccess = {
                    screenState = screenState.copy(isAddingLocation = false)
                    clearAddEditState()
                    onSuccess()
                },
                onError = { message ->
                    screenState = screenState.copy(isAddingLocation = false)
                    onError(message)
                }
            )
        }
    }

    fun updateLocation(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!addEditState.isEditMode || addEditState.locationId == null) return

        viewModelScope.launch {
            screenState = screenState.copy(isUpdatingLocation = true)

            val locationToUpdate = Location(
                id = addEditState.locationId!!,
                userId = locationRepository.getCurrentUserId() ?: "",
                title = addEditState.title,
                fullAddress = addEditState.fullAddress,
                city = addEditState.city,
                state = addEditState.state.takeIf { it.isNotBlank() },
                postalCode = addEditState.postalCode,
                country = addEditState.country,
                isDefault = addEditState.isDefault,
                latitude = addEditState.latitude,
                longitude = addEditState.longitude
            )

            locationRepository.updateLocation(
                location = locationToUpdate,
                onSuccess = {
                    screenState = screenState.copy(isUpdatingLocation = false)
                    clearAddEditState()
                    onSuccess()
                },
                onError = { message ->
                    screenState = screenState.copy(isUpdatingLocation = false)
                    onError(message)
                }
            )
        }
    }

    fun deleteLocation(
        location: Location,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            screenState = screenState.copy(isDeletingLocation = true)

            locationRepository.deleteLocation(
                locationId = location.id,
                onSuccess = {
                    screenState = screenState.copy(
                        isDeletingLocation = false,
                        showDeleteConfirmDialog = false,
                        selectedLocationForDelete = null
                    )
                    onSuccess()
                },
                onError = { message ->
                    screenState = screenState.copy(isDeletingLocation = false)
                    onError(message)
                }
            )
        }
    }

    fun setDefaultLocation(
        location: Location,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            locationRepository.setDefaultLocation(
                locationId = location.id,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    // UI State Management Methods
    fun showDeleteConfirmDialog(location: Location) {
        screenState = screenState.copy(
            selectedLocationForDelete = location,
            showDeleteConfirmDialog = true
        )
    }

    fun hideDeleteConfirmDialog() {
        screenState = screenState.copy(
            selectedLocationForDelete = null,
            showDeleteConfirmDialog = false
        )
    }

    fun startAddingLocation() {
        addEditState = AddEditLocationState(isEditMode = false)
    }

    fun startEditingLocation(location: Location) {
        addEditState = AddEditLocationState(
            isEditMode = true,
            locationId = location.id,
            title = location.title,
            fullAddress = location.fullAddress,
            city = location.city,
            state = location.state ?: "",
            postalCode = location.postalCode,
            country = location.country,
            isDefault = location.isDefault,
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    fun clearAddEditState() {
        addEditState = AddEditLocationState()
    }

    // Form Field Updates
    fun updateTitle(title: String) {
        addEditState = addEditState.copy(title = title)
    }

    fun updateFullAddress(address: String) {
        addEditState = addEditState.copy(fullAddress = address)
    }

    fun updateCity(city: String) {
        addEditState = addEditState.copy(city = city)
    }

    fun updateState(state: String) {
        addEditState = addEditState.copy(state = state)
    }

    fun updatePostalCode(postalCode: String) {
        addEditState = addEditState.copy(postalCode = postalCode)
    }

    fun updateCountry(country: String) {
        addEditState = addEditState.copy(country = country)
    }

    fun updateIsDefault(isDefault: Boolean) {
        addEditState = addEditState.copy(isDefault = isDefault)
    }

    fun updateCoordinates(latitude: Double?, longitude: Double?) {
        addEditState = addEditState.copy(
            latitude = latitude,
            longitude = longitude
        )
    }
} 