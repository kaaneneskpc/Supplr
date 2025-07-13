package com.kaaneneskpc.supplr.admin_panel

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import org.jetbrains.compose.resources.painterResource

@Composable
fun AdminPanelScreen(
    navigateBack: () -> Unit,
    navigateToManageProduct: (String?) -> Unit
) {
    CommonScaffold(
        title = "Admin Panel",
        navigateBack = navigateBack,
        actions = {
            IconButton(onClick = { /* TODO: Add action */ }) {
                Icon(
                    painter = painterResource(Resources.Icon.Search),
                    contentDescription = "Search Icon",
                    tint = IconPrimary
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToManageProduct(null) },
                containerColor = ButtonPrimary,
                contentColor = TextPrimary,
                content = {
                    Icon(
                        painter = painterResource(Resources.Icon.Plus),
                        contentDescription = "Add Icon"
                    )
                }
            )
        }
    ) {

    }
}
