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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.dialog.CountryPickerDialog
import com.kaaneneskpc.supplr.shared.domain.Country
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter

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

    Box(modifier = modifier.fillMaxSize().background(Surface)) {
        Card(
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .verticalScroll(state = rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Profile Avatar",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "First Name", style = MaterialTheme.typography.labelMedium)
                        CustomTextField(
                            value = firstName,
                            onValueChange = onFirstNameChange,
                            placeholder = "First Name",
                            error = firstName.length !in 3..50
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Last Name", style = MaterialTheme.typography.labelMedium)
                        CustomTextField(
                            value = lastName,
                            onValueChange = onLastNameChange,
                            placeholder = "Last Name",
                            error = lastName.length !in 3..50
                        )
                    }
                }
                Column {
                    Text(text = "E-Mail", style = MaterialTheme.typography.labelMedium)
                    CustomTextField(
                        value = email,
                        onValueChange = {},
                        placeholder = "E-Mail",
                        enabled = false
                    )
                }
                Column {
                    Text(text = "City", style = MaterialTheme.typography.labelMedium)
                    CustomTextField(
                        value = city ?: "",
                        onValueChange = onCityChange,
                        placeholder = "City",
                        error = city?.length !in 3..50
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Postal Code", style = MaterialTheme.typography.labelMedium)
                        CustomTextField(
                            value = "${postalCode ?: ""}",
                            onValueChange = { onPostalCodeChange(it.toIntOrNull()) },
                            placeholder = "Postal Code",
                            error = postalCode == null || postalCode.toString().length !in 3..8,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Column(modifier = Modifier.weight(2f)) {
                        Text(text = "Address", style = MaterialTheme.typography.labelMedium)
                        CustomTextField(
                            value = address ?: "",
                            onValueChange = onAddressChange,
                            placeholder = "Address",
                            error = address?.length !in 3..50
                        )
                    }
                }
                Column {
                    Text(text = "Telefon", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AlertTextField(
                            text = "+${country.dialCode}",
                            icon = country.flag,
                            onClick = { showCountryDialog = true }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        CustomTextField(
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
    }
}