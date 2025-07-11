package com.kaaneneskpc.supplr.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.ProfileForm
import com.kaaneneskpc.supplr.shared.component.SupplrButton
import com.kaaneneskpc.supplr.shared.domain.Country
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.background(Surface).systemBarsPadding()) {
        ProfileForm(
            firstName = "Kaan Enes",
            onFirstNameChange = {},
            lastName = "Kapici",
            onLastNameChange = {},
            email = "",
            country = Country.Turkey,
            onCountrySelect = {},
            city = "Istanbul",
            onCityChange = {},
            postalCode = 34000,
            onPostalCodeChange = {},
            address = "123 Main St",
            onAddressChange = {},
            phoneNumber = "+90 123 456 7890",
            onPhoneNumberChange = {},
        )

    }
}