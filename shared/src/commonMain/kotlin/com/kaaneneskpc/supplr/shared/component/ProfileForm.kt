package com.kaaneneskpc.supplr.shared.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.dialog.CountryPickerDialog
import com.kaaneneskpc.supplr.shared.domain.Country
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlue
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlueLighter
import com.kaaneneskpc.supplr.shared.fonts.Surface as SurfaceColor
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary

@Composable
fun ProfileForm(
    modifier: Modifier = Modifier,
    country: Country,
    onCountrySelect: (Country) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    city: String?,
    onCityChange: (String) -> Unit,
    postalCode: Int?,
    onPostalCodeChange: (Int?) -> Unit,
    address: String?,
    onAddressChange: (String) -> Unit,
    phoneNumber: String?,
    onPhoneNumberChange: (String) -> Unit,
) {
    var showCountryDialog by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = showCountryDialog) {
        CountryPickerDialog(
            country = country,
            onDismiss = { showCountryDialog = false },
            onConfirmClick = { selectedCountry ->
                showCountryDialog = false
                onCountrySelect(selectedCountry)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceColor
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CategoryBlueLighter.copy(alpha = 0.3f),
                                SurfaceColor
                            )
                        )
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = CategoryBlue.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile Avatar",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(12.dp),
                            tint = CategoryBlue
                        )
                    }
                    Text(
                        text = "My Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Manage your personal information",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }

        ProfileSection(
            title = "Personal Information",
            icon = Icons.Filled.Person
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileFieldWithIcon(
                        modifier = Modifier.weight(1f),
                        label = "First Name",
                        value = firstName,
                        onValueChange = onFirstNameChange,
                        placeholder = "First Name",
                        error = firstName.length !in 3..50,
                        icon = Icons.Filled.Person
                    )
                    ProfileFieldWithIcon(
                        modifier = Modifier.weight(1f),
                        label = "Last Name",
                        value = lastName,
                        onValueChange = onLastNameChange,
                        placeholder = "Last Name",
                        error = lastName.length !in 3..50,
                        icon = Icons.Filled.Person
                    )
                }
                
                ProfileFieldWithIcon(
                    label = "E-Mail",
                    value = email,
                    onValueChange = {},
                    placeholder = "E-Mail",
                    enabled = false,
                    icon = Icons.Filled.Email
                )
            }
        }

        ProfileSection(
            title = "Contact Information",
            icon = Icons.Filled.Phone
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(
                        text = "Phone Number",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AlertTextField(
                            text = "+${country.dialCode}",
                            icon = country.flag,
                            onClick = { showCountryDialog = true }
                        )
                        CustomTextField(
                            modifier = Modifier.weight(1f),
                            value = phoneNumber ?: "",
                            onValueChange = onPhoneNumberChange,
                            placeholder = "Phone Number",
                            error = phoneNumber.toString().length !in 5..30,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }
        }

        ProfileSection(
            title = "Address Information",
            icon = Icons.Filled.LocationOn
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileFieldWithIcon(
                    label = "City",
                    value = city ?: "",
                    onValueChange = onCityChange,
                    placeholder = "City",
                    error = city?.length !in 3..50,
                    icon = Icons.Filled.LocationOn
                )

                ProfileFieldWithIcon(
                    label = "Postal Code",
                    value = "${postalCode ?: ""}",
                    onValueChange = { onPostalCodeChange(it.toIntOrNull()) },
                    placeholder = "Postal Code",
                    error = postalCode == null || postalCode.toString().length !in 3..8,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    icon = Icons.Filled.LocationOn
                )
                
                ProfileFieldWithIcon(
                    label = "Address",
                    value = address ?: "",
                    onValueChange = onAddressChange,
                    placeholder = "Address",
                    error = address?.length !in 3..50,
                    icon = Icons.Filled.Home
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = CategoryBlue.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(6.dp),
                        tint = CategoryBlue
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 16.dp),
                color = SurfaceLighter
            )
            content()
        }
    }
}

@Composable
private fun ProfileFieldWithIcon(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    icon: ImageVector
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = CategoryBlue.copy(alpha = 0.7f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )
        }
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            error = error,
            enabled = enabled,
            keyboardOptions = keyboardOptions
        )
    }
}