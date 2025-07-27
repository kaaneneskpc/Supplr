package com.kaaneneskpc.supplr.locations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.supplr.data.domain.LocationRepository
import com.kaaneneskpc.supplr.shared.domain.Location
import com.kaaneneskpc.supplr.shared.domain.LocationCategory
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
    val category: LocationCategory = LocationCategory.OTHER,
    val fullAddress: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = "",
    val isDefault: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class ValidationErrors(
    val title: String? = null,
    val fullAddress: String? = null,
    val city: String? = null,
    val postalCode: String? = null,
    val country: String? = null
)

class LocationsViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    var screenState: LocationsScreenState by mutableStateOf(LocationsScreenState())
        private set

    var addEditState: AddEditLocationState by mutableStateOf(AddEditLocationState())
        private set

    var validationErrors: ValidationErrors by mutableStateOf(ValidationErrors())
        private set

    val isAddEditFormValid: Boolean
        get() = validateForm().let { errors ->
            validationErrors = errors
            errors.title == null && errors.fullAddress == null && 
            errors.city == null && errors.postalCode == null && errors.country == null
        }

    init {
        loadUserLocations()
    }

    private fun validateForm(): ValidationErrors {
        val errors = ValidationErrors()
        
        return errors.copy(
            title = when {
                addEditState.title.isBlank() -> "Title is required"
                addEditState.title.length < 2 -> "Title must be at least 2 characters"
                addEditState.title.length > 50 -> "Title must be less than 50 characters"
                else -> null
            },
            fullAddress = when {
                addEditState.fullAddress.isBlank() -> "Address is required"
                addEditState.fullAddress.length < 5 -> "Address must be at least 5 characters"
                addEditState.fullAddress.length > 200 -> "Address must be less than 200 characters"
                else -> null
            },
            city = when {
                addEditState.city.isBlank() -> "City is required"
                addEditState.city.length < 2 -> "City must be at least 2 characters"
                addEditState.city.length > 50 -> "City must be less than 50 characters"
                !addEditState.city.matches(Regex("^[a-zA-Z\\s\\-']+$")) -> "City contains invalid characters"
                else -> null
            },
            postalCode = when {
                addEditState.postalCode.isBlank() -> "Postal code is required"
                !isValidPostalCode(addEditState.postalCode, addEditState.country) -> "Invalid postal code format"
                else -> null
            },
            country = when {
                addEditState.country.isBlank() -> "Country is required"
                else -> null
            }
        )
    }

    private fun isValidPostalCode(postalCode: String, country: String): Boolean {
        return when (country.uppercase()) {
            "TURKEY" -> postalCode.matches(Regex("^\\d{5}$"))
            "USA" -> postalCode.matches(Regex("^\\d{5}(-\\d{4})?$"))
            "INDIA" -> postalCode.matches(Regex("^\\d{6}$"))
            "SERBIA" -> postalCode.matches(Regex("^\\d{5}$"))
            else -> postalCode.matches(Regex("^[A-Za-z0-9\\s-]{3,10}$"))
        }
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
        if (!isAddEditFormValid) {
            onError("Please fix validation errors")
            return
        }

        viewModelScope.launch {
            screenState = screenState.copy(isAddingLocation = true)
            
            locationRepository.addLocation(
                title = addEditState.title.trim(),
                category = addEditState.category,
                fullAddress = addEditState.fullAddress.trim(),
                city = addEditState.city.trim(),
                state = addEditState.state.trim().takeIf { it.isNotBlank() },
                postalCode = addEditState.postalCode.trim(),
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
        if (!isAddEditFormValid) {
            onError("Please fix validation errors")
            return
        }

        viewModelScope.launch {
            screenState = screenState.copy(isUpdatingLocation = true)

            val locationToUpdate = Location(
                id = addEditState.locationId!!,
                userId = locationRepository.getCurrentUserId() ?: "",
                title = addEditState.title.trim(),
                category = addEditState.category,
                fullAddress = addEditState.fullAddress.trim(),
                city = addEditState.city.trim(),
                state = addEditState.state.trim().takeIf { it.isNotBlank() },
                postalCode = addEditState.postalCode.trim(),
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
        validationErrors = ValidationErrors()
    }

    fun startEditingLocation(location: Location) {
        addEditState = AddEditLocationState(
            isEditMode = true,
            locationId = location.id,
            title = location.title,
            category = location.category,
            fullAddress = location.fullAddress,
            city = location.city,
            state = location.state ?: "",
            postalCode = location.postalCode,
            country = location.country,
            isDefault = location.isDefault,
            latitude = location.latitude,
            longitude = location.longitude
        )
        validationErrors = ValidationErrors()
    }

    fun clearAddEditState() {
        addEditState = AddEditLocationState()
        validationErrors = ValidationErrors()
    }

    fun updateTitle(title: String) {
        addEditState = addEditState.copy(title = title)
        if (validationErrors.title != null) {
            validationErrors = validationErrors.copy(title = null)
        }
    }

    fun updateCategory(category: LocationCategory) {
        addEditState = addEditState.copy(category = category)
    }

    fun updateFullAddress(address: String) {
        addEditState = addEditState.copy(fullAddress = address)
        if (validationErrors.fullAddress != null) {
            validationErrors = validationErrors.copy(fullAddress = null)
        }
    }

    fun updateCity(city: String) {
        addEditState = addEditState.copy(city = city)
        if (validationErrors.city != null) {
            validationErrors = validationErrors.copy(city = null)
        }
    }

    fun updateState(state: String) {
        addEditState = addEditState.copy(state = state)
    }

    fun updatePostalCode(postalCode: String) {
        addEditState = addEditState.copy(postalCode = postalCode)
        if (validationErrors.postalCode != null) {
            validationErrors = validationErrors.copy(postalCode = null)
        }
    }

    fun updateCountry(country: String) {
        addEditState = addEditState.copy(country = country)
        if (validationErrors.country != null) {
            validationErrors = validationErrors.copy(country = null)
        }
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

    fun getValidationError(field: String): String? {
        return when (field) {
            "title" -> validationErrors.title
            "fullAddress" -> validationErrors.fullAddress
            "city" -> validationErrors.city
            "postalCode" -> validationErrors.postalCode
            "country" -> validationErrors.country
            else -> null
        }
    }
} 