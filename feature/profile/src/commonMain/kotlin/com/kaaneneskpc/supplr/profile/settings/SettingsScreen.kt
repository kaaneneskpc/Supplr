package com.kaaneneskpc.supplr.profile.settings

import ContentWithMessageBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.InfoCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlue
import com.kaaneneskpc.supplr.shared.fonts.CategoryRed
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextWhite
import com.kaaneneskpc.supplr.shared.util.DisplayResult
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    navigateToChangePassword: () -> Unit,
    navigateToTwoFactorAuth: () -> Unit,
    navigateToDeleteAccount: () -> Unit
) {
    val viewModel = koinViewModel<SettingsViewModel>()
    val screenState = viewModel.screenState
    val screenReady = viewModel.screenReady
    val messageBarState = rememberMessageBarState()

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ) {
            screenReady.DisplayResult(
                onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
                onSuccess = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        item {
                            SettingsSection(title = "Communication Preferences") {
                                SettingsToggleItem(
                                    icon = Icons.Default.Email,
                                    title = "Email Notifications",
                                    subtitle = "Receive updates and promotions via email",
                                    isChecked = screenState.isEmailEnabled,
                                    onCheckedChange = viewModel::updateEmailPreference
                                )
                                HorizontalDivider(color = SurfaceLighter)
                                SettingsToggleItem(
                                    icon = Icons.Default.Notifications,
                                    title = "Push Notifications",
                                    subtitle = "Receive push notifications on your device",
                                    isChecked = screenState.isPushEnabled,
                                    onCheckedChange = viewModel::updatePushPreference
                                )
                                HorizontalDivider(color = SurfaceLighter)
                                SettingsToggleItem(
                                    icon = Icons.Default.Email,
                                    title = "SMS Notifications",
                                    subtitle = "Receive SMS messages for important updates",
                                    isChecked = screenState.isSmsEnabled,
                                    onCheckedChange = viewModel::updateSmsPreference
                                )
                            }
                        }
                        item {
                            SettingsSection(title = "Security") {
                                SettingsNavigationItem(
                                    icon = Resources.Icon.Lock,
                                    title = "Change Password",
                                    subtitle = "Update your account password",
                                    onClick = navigateToChangePassword
                                )
                                HorizontalDivider(color = SurfaceLighter)
                                SettingsNavigationItem(
                                    icon = Resources.Icon.Shield,
                                    title = "Two-Factor Authentication",
                                    subtitle = if (screenState.isTwoFactorEnabled) "Enabled" else "Disabled",
                                    onClick = navigateToTwoFactorAuth
                                )
                            }
                        }
                        item {
                            SettingsSection(title = "Account", isDanger = true) {
                                SettingsNavigationItem(
                                    icon = Resources.Icon.Trash,
                                    title = "Delete Account",
                                    subtitle = "Permanently delete your account and data",
                                    onClick = navigateToDeleteAccount,
                                    isDanger = true
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                    }
                },
                onError = { errorMessage ->
                    InfoCard(
                        image = Resources.Image.Cat,
                        title = "Oops!",
                        subtitle = errorMessage
                    )
                }
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    isDanger: Boolean = false,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDanger) CategoryRed else TextPrimary
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(CategoryBlue.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = CategoryBlue
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary.copy(alpha = 0.6f)
                )
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = CategoryBlue,
                uncheckedThumbColor = TextPrimary,
                uncheckedTrackColor = SurfaceLighter
            )
        )
    }
}

@Composable
private fun SettingsNavigationItem(
    icon: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDanger: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isDanger) CategoryRed.copy(alpha = 0.1f) else CategoryBlue.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isDanger) CategoryRed else CategoryBlue
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isDanger) CategoryRed else TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDanger) CategoryRed.copy(alpha = 0.6f) else TextPrimary.copy(alpha = 0.6f)
                )
            )
        }
        Icon(
            painter = painterResource(Resources.Icon.RightArrow),
            contentDescription = "Navigate",
            modifier = Modifier.size(20.dp),
            tint = if (isDanger) CategoryRed else TextPrimary.copy(alpha = 0.5f)
        )
    }
}
