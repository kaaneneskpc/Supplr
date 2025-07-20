package com.kaaneneskpc.supplr.contact_us

import ContentWithMessageBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.contact_us.component.ContactItem
import com.kaaneneskpc.supplr.contact_us.component.SocialIcon
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextWhite
import rememberMessageBarState

@Composable
fun ContactUsScreen(
    navigateBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    CommonScaffold(
        title = "Contact Us",
        navigateBack = navigateBack,
    ) { paddingValues ->
        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            messageBarState = rememberMessageBarState(),
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    ContactItem(
                        icon = Icons.Default.Star,
                        label = "Website",
                        value = "supplr.com",
                        onClick = { uriHandler.openUri("https://supplr.com") }
                    )
                    ContactItem(
                        icon = Icons.Default.Email,
                        label = "E-mail",
                        value = "info@supplr.com",
                        onClick = { uriHandler.openUri("mailto:info@supplr.com") }
                    )
                    ContactItem(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = "+90 555 555 55 55",
                        onClick = { uriHandler.openUri("tel:+905555555555") }
                    )
                    ContactItem(
                        icon = Icons.Default.LocationOn,
                        label = "Address",
                        value = "Örnek Mah. Supplr Cad. No:1, İstanbul, Türkiye",
                        onClick = { /* Optionally open maps */ }
                    )
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SocialIcon(
                            icon = Icons.Default.Person,
                            contentDescription = "Facebook",
                            color = Color(0xFF4267B2),
                            onClick = { uriHandler.openUri("https://facebook.com/supplr") },
                            text = "Facebook"
                        )
                        SocialIcon(
                            icon = Icons.Default.Person,
                            contentDescription = "Instagram",
                            color = Color(0xFFE1306C),
                            onClick = { uriHandler.openUri("https://instagram.com/supplr") },
                            text = "Instagram"
                        )
                        SocialIcon(
                            icon = Icons.Default.Person,
                            contentDescription = "Twitter",
                            color = Color(0xFF1DA1F2),
                            onClick = { uriHandler.openUri("https://twitter.com/supplr") },
                            text = "Twitter"
                        )
                    }
                }
            }
        }
    }
}



