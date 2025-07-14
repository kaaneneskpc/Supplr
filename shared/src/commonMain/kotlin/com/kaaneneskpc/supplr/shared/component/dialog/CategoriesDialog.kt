package com.kaaneneskpc.supplr.shared.component.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.domain.ProductCategory
import com.kaaneneskpc.supplr.shared.fonts.Alpha
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextSecondary
import org.jetbrains.compose.resources.painterResource

@Composable
fun CategoriesDialog(
    category: ProductCategory,
    onDismiss: () -> Unit,
    onConfirmClick: (ProductCategory) -> Unit,
) {
    var selectedCategory by remember(category) { mutableStateOf(category) }
    val scrollState = rememberScrollState()
    AlertDialog(
        containerColor = Surface,
        title = {
            Text(
                text = "Select a Category",
                fontSize = FontSize.EXTRA_MEDIUM,
                color = TextPrimary
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    ProductCategory.entries.forEach { currentCategory ->
                        val animatedBackground by animateColorAsState(
                            targetValue = if (currentCategory == selectedCategory) currentCategory.color.copy(
                                alpha = Alpha.TWENTY_PERCENT
                            )
                            else Color.Transparent
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(size = 6.dp))
                                .background(animatedBackground)
                                .clickable { selectedCategory = currentCategory }
                                .padding(
                                    vertical = 16.dp,
                                    horizontal = 12.dp
                                )
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = currentCategory.title,
                                color = TextPrimary,
                                fontSize = FontSize.REGULAR
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            AnimatedVisibility(
                                visible = selectedCategory == currentCategory
                            ) {
                                Icon(
                                    modifier = Modifier.size(14.dp),
                                    painter = painterResource(Resources.Icon.Checkmark),
                                    contentDescription = "Checkmark icon",
                                    tint = IconPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirmClick(selectedCategory) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextSecondary
                )
            ) {
                Text(
                    text = "Confirm",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary.copy(alpha = Alpha.HALF)
                )
            ) {
                Text(
                    text = "Cancel",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}