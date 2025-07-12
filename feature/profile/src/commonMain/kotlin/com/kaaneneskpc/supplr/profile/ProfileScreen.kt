package com.kaaneneskpc.supplr.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.ProfileForm
import com.kaaneneskpc.supplr.shared.component.SupplrButton
import com.kaaneneskpc.supplr.shared.domain.Country
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateBack: () -> Unit
) {
    var country by remember { mutableStateOf(Country.Turkey) }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontSize = FontSize.LARGE,
                        fontFamily = BebasNeueFont(),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back Arrow Icon",
                            tint = IconPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(
                top = paddingValues.calculateBottomPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            ProfileForm(
                modifier = Modifier.weight(1f),
                firstName = "Kaan Enes",
                onFirstNameChange = {},
                lastName = "Kapici",
                onLastNameChange = {},
                email = "",
                country = country,
                onCountrySelect = {
                    country = it
                },
                city = "Istanbul",
                onCityChange = {},
                postalCode = 34000,
                onPostalCodeChange = {},
                address = "123 Main St",
                onAddressChange = {},
                phoneNumber = "+90 123 456 7890",
                onPhoneNumberChange = {},
            )
            Spacer(modifier = Modifier.height(12.dp))
            SupplrButton(
                text = "Update",
                icon = Resources.Icon.Checkmark,
                onClick = {}
            )
        }
    }
}