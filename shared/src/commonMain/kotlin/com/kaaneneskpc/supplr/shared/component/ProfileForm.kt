package com.kaaneneskpc.supplr.shared.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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

    AnimatedVisibility(
        visible = showCountryDialog
    ) {
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
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextField(
                modifier = Modifier.weight(1f),
                value = firstName,
                onValueChange = onFirstNameChange,
                label = "First Name",
                leadingIcon = Icons.Default.Person,
                error = firstName.length !in 3..50
            )
            CustomTextField(
                modifier = Modifier.weight(1f),
                value = lastName,
                onValueChange = onLastNameChange,
                label = "Last Name",
                leadingIcon = Icons.Default.Person,
                error = lastName.length !in 3..50
            )
        }
        CustomTextField(
            value = email,
            onValueChange = {},
            label = "Email",
            leadingIcon = Icons.Default.Email,
            enabled = false
        )
        CustomTextField(
            value = city ?: "",
            onValueChange = onCityChange,
            label = "City",
            leadingIcon = Icons.Default.LocationOn,
            error = city?.length !in 3..50
        )
        CustomTextField(
            value = "${postalCode ?: ""}",
            onValueChange = { onPostalCodeChange(it.toIntOrNull()) },
            label = "Postal Code",
            leadingIcon = Icons.Default.Call,
            error = postalCode == null || postalCode.toString().length !in 3..8,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        CustomTextField(
            value = address ?: "",
            onValueChange = onAddressChange,
            label = "Address",
            leadingIcon = Icons.Default.LocationOn,
            error = address?.length !in 3..50
        )
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
                label = "Phone Number",
                leadingIcon = Icons.Default.Phone,
                error = phoneNumber.toString().length !in 5..30,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}