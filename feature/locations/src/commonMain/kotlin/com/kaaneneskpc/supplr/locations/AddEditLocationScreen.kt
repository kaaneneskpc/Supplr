package com.kaaneneskpc.supplr.locations

import ContentWithMessageBar
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.fonts.*
import com.kaaneneskpc.supplr.shared.util.RequestState
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun AddEditLocationScreen(
    locationId: String?,
    navigateBack: () -> Unit
) {
    val messageBarState = rememberMessageBarState()
    val locationsViewModel = koinViewModel<LocationsViewModel>()
    val addEditState = locationsViewModel.addEditState
    val screenState = locationsViewModel.screenState

    val isEditMode = locationId != null
    val title = if (isEditMode) "Edit Address" else "Add New Address"

    // Initialize edit mode if locationId is provided
    LaunchedEffect(locationId) {
        if (locationId != null && !addEditState.isEditMode) {
            // Find the location from the current list and populate edit state
            val locations = screenState.locations
            if (locations is RequestState.Success) {
                val location = locations.data.find { it.id == locationId }
                if (location != null) {
                    locationsViewModel.startEditingLocation(location)
                }
            }
        } else if (locationId == null && !addEditState.isEditMode) {
            locationsViewModel.startAddingLocation()
        }
    }

    // Animation for the entire screen
    val animatedVisibility by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = EaseOutCubic)
    )

    CommonScaffold(
        title = title,
        navigateBack = {
            locationsViewModel.clearAddEditState()
            navigateBack()
        }
    ) { paddingValues ->
        ContentWithMessageBar(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            contentBackgroundColor = Surface,
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Surface,
                                SurfaceLighter,
                                Surface
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .scale(animatedVisibility),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header Section
                    AddEditHeader(isEditMode = isEditMode)

                    // Address Title Section
                    AddressTitleSection(
                        title = addEditState.title,
                        onTitleChange = locationsViewModel::updateTitle
                    )

                    // Address Details Section
                    AddressDetailsSection(
                        fullAddress = addEditState.fullAddress,
                        city = addEditState.city,
                        state = addEditState.state,
                        postalCode = addEditState.postalCode,
                        country = addEditState.country,
                        onFullAddressChange = locationsViewModel::updateFullAddress,
                        onCityChange = locationsViewModel::updateCity,
                        onStateChange = locationsViewModel::updateState,
                        onPostalCodeChange = locationsViewModel::updatePostalCode,
                        onCountryChange = locationsViewModel::updateCountry
                    )

                    // Default Address Section
                    DefaultAddressSection(
                        isDefault = addEditState.isDefault,
                        onIsDefaultChange = locationsViewModel::updateIsDefault
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Submit Button
                    SubmitButton(
                        isEditMode = isEditMode,
                        isEnabled = locationsViewModel.isAddEditFormValid,
                        isSubmitting = screenState.isAddingLocation || screenState.isUpdatingLocation,
                        onClick = {
                            if (isEditMode) {
                                locationsViewModel.updateLocation(
                                    onSuccess = {
                                        messageBarState.addSuccess("✅ Address updated successfully!")
                                        navigateBack()
                                    },
                                    onError = { error ->
                                        messageBarState.addError(error)
                                    }
                                )
                            } else {
                                locationsViewModel.addLocation(
                                    onSuccess = {
                                        messageBarState.addSuccess("🎉 Address added successfully!")
                                        navigateBack()
                                    },
                                    onError = { error ->
                                        messageBarState.addError(error)
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddEditHeader(isEditMode: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = SurfaceBrand.copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SurfaceBrand.copy(alpha = 0.1f),
                            Surface,
                            SurfaceBrand.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEditMode) "✏️ Edit Address" else "🏠 Add New Address",
                    fontSize = FontSize.EXTRA_LARGE,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BebasNeueFont(),
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isEditMode)
                        "Update your delivery address details"
                    else
                        "Fill in the details for your new delivery address",
                    fontSize = FontSize.SMALL,
                    fontFamily = RobotoCondensedFont(),
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AddressTitleSection(
    title: String,
    onTitleChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = TextSecondary.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "🏷️ Address Title",
                fontSize = FontSize.LARGE,
                fontWeight = FontWeight.SemiBold,
                fontFamily = BebasNeueFont(),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Give this address a memorable name (e.g., Home, Office, Mom's House)",
                fontSize = FontSize.SMALL,
                fontFamily = RobotoCondensedFont(),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "e.g., Home, Office, Work",
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontFamily = RobotoCondensedFont(),
                        fontSize = FontSize.MEDIUM
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SurfaceBrand,
                    unfocusedBorderColor = BorderIdle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = SurfaceBrand,
                    focusedContainerColor = SurfaceBrand.copy(alpha = 0.05f),
                    unfocusedContainerColor = SurfaceLighter
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Title",
                        tint = SurfaceBrand
                    )
                }
            )
        }
    }
}

@Composable
private fun AddressDetailsSection(
    fullAddress: String,
    city: String,
    state: String,
    postalCode: String,
    country: String,
    onFullAddressChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onPostalCodeChange: (String) -> Unit,
    onCountryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = TextSecondary.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "📍 Address Details",
                fontSize = FontSize.LARGE,
                fontWeight = FontWeight.SemiBold,
                fontFamily = BebasNeueFont(),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Full Address
            OutlinedTextField(
                value = fullAddress,
                onValueChange = onFullAddressChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = "Enter your complete street address including apartment/unit number...",
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontFamily = RobotoCondensedFont(),
                        fontSize = FontSize.SMALL
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SurfaceBrand,
                    unfocusedBorderColor = BorderIdle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = SurfaceBrand,
                    focusedContainerColor = SurfaceBrand.copy(alpha = 0.05f),
                    unfocusedContainerColor = SurfaceLighter
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 4,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = SurfaceBrand
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // City and State Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = onCityChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "City",
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontFamily = RobotoCondensedFont()
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SurfaceBrand,
                        unfocusedBorderColor = BorderIdle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = SurfaceBrand,
                        focusedContainerColor = SurfaceBrand.copy(alpha = 0.05f),
                        unfocusedContainerColor = SurfaceLighter
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "City",
                            tint = SurfaceBrand
                        )
                    }
                )

                OutlinedTextField(
                    value = state,
                    onValueChange = onStateChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "State (Optional)",
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontFamily = RobotoCondensedFont()
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SurfaceBrand,
                        unfocusedBorderColor = BorderIdle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = SurfaceBrand,
                        focusedContainerColor = SurfaceBrand.copy(alpha = 0.05f),
                        unfocusedContainerColor = SurfaceLighter
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "State",
                            tint = SurfaceBrand
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Postal Code and Country Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = onPostalCodeChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Postal Code",
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontFamily = RobotoCondensedFont()
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SurfaceBrand,
                        unfocusedBorderColor = BorderIdle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = SurfaceBrand,
                        focusedContainerColor = SurfaceBrand.copy(alpha = 0.05f),
                        unfocusedContainerColor = SurfaceLighter
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Postal Code",
                            tint = SurfaceBrand
                        )
                    }
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = onCountryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Country",
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontFamily = RobotoCondensedFont()
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SurfaceBrand,
                        unfocusedBorderColor = BorderIdle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = SurfaceBrand,
                        focusedContainerColor = SurfaceBrand.copy(alpha = 0.05f),
                        unfocusedContainerColor = SurfaceLighter
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Country",
                            tint = SurfaceBrand
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DefaultAddressSection(
    isDefault: Boolean,
    onIsDefaultChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isDefault) SurfaceBrand.copy(alpha = 0.3f) else TextSecondary.copy(
                    alpha = 0.2f
                )
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDefault) {
                        Brush.linearGradient(
                            colors = listOf(
                                SurfaceBrand.copy(alpha = 0.1f),
                                Surface
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                SurfaceLighter.copy(alpha = 0.5f),
                                Surface
                            )
                        )
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "⭐ Set as Default Address",
                        fontSize = FontSize.MEDIUM,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = RobotoCondensedFont(),
                        color = TextPrimary
                    )
                    Text(
                        text = "This will be your primary delivery address",
                        fontSize = FontSize.SMALL,
                        fontFamily = RobotoCondensedFont(),
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = isDefault,
                    onCheckedChange = onIsDefaultChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SurfaceBrand,
                        checkedTrackColor = SurfaceBrand.copy(alpha = 0.5f),
                        uncheckedThumbColor = BorderIdle,
                        uncheckedTrackColor = SurfaceLighter
                    )
                )
            }
        }
    }
}

@Composable
private fun SubmitButton(
    isEditMode: Boolean,
    isEnabled: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit
) {
    val buttonText = when {
        isSubmitting && isEditMode -> "✨ Updating Address..."
        isSubmitting && !isEditMode -> "✨ Adding Address..."
        isEditMode -> "💾 Update Address"
        else -> "🏠 Add Address"
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isEnabled) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier.scale(animatedScale)
    ) {
        if (isSubmitting) {
            Button(
                onClick = { },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceBrand.copy(alpha = 0.7f),
                    disabledContainerColor = SurfaceBrand.copy(alpha = 0.7f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = buttonText,
                        fontSize = FontSize.MEDIUM,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = RobotoCondensedFont(),
                        color = TextPrimary
                    )
                }
            }
        } else {
            Button(
                onClick = onClick,
                enabled = isEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = if (isEnabled) 8.dp else 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = if (isEnabled) SurfaceBrand.copy(alpha = 0.3f) else Color.Transparent
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEnabled) SurfaceBrand else ButtonDisabled,
                    contentColor = TextPrimary,
                    disabledContainerColor = ButtonDisabled,
                    disabledContentColor = TextPrimary.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = RobotoCondensedFont()
                )
            }
        }
    }
} 