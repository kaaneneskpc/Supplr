package com.kaaneneskpc.supplr.manage_product

import ContentWithMessageBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kaaneneskpc.supplr.shared.component.AlertTextField
import com.kaaneneskpc.supplr.shared.component.CommonScaffold
import com.kaaneneskpc.supplr.shared.component.CustomTextField
import com.kaaneneskpc.supplr.shared.component.ErrorCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.component.SupplrButton
import com.kaaneneskpc.supplr.shared.component.dialog.CategoriesDialog
import com.kaaneneskpc.supplr.shared.domain.ProductCategory
import com.kaaneneskpc.supplr.shared.fonts.BorderIdle
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceDarker
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.SurfaceSecondary
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.fonts.TextSecondary
import com.kaaneneskpc.supplr.shared.fonts.TextWhite
import com.kaaneneskpc.supplr.shared.util.DisplayResult
import com.kaaneneskpc.supplr.shared.util.RequestState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun ManageProductScreen(
    id: String?,
    navigateBack: () -> Unit
) {
    val messageBarState = rememberMessageBarState()
    val manageProductViewModel = koinViewModel<ManageProductViewModel>()
    val screenState = manageProductViewModel.screenState
    val isFormValid = manageProductViewModel.isFormValid
    val thumbnailUploaderState = manageProductViewModel.thumbnailUploaderState
    var showCategoriesDialog by remember { mutableStateOf(false) }
    var dropdownMenuOpened by remember { mutableStateOf(false) }

    val photoPicker = koinInject<PhotoPicker>()

    photoPicker.InitializePhotoPicker(
        onImageSelect = { file ->
            manageProductViewModel.uploadThumbnailToStorage(
                file = file,
                onSuccess = { messageBarState.addSuccess("Thumbnail uploaded successfully!") }
            )
        }
    )

    AnimatedVisibility(
        visible = showCategoriesDialog
    ) {
        CategoriesDialog(
            category = screenState.category,
            onDismiss = { showCategoriesDialog = false },
            onConfirmClick = { selectedCategory ->
                manageProductViewModel.updateCategory(selectedCategory)
                showCategoriesDialog = false
            }
        )
    }

    CommonScaffold(
        title = if (id == null) "New Product" else "Edit Product",
        navigateBack = navigateBack,
        actions = {
            id.takeIf { it != null }?.let {
                Box {
                    IconButton(onClick = { dropdownMenuOpened = true }) {
                        Icon(
                            painter = painterResource(Resources.Icon.VerticalMenu),
                            contentDescription = "Vertical menu icon",
                            tint = IconPrimary
                        )
                    }
                    DropdownMenu(
                        containerColor = Surface,
                        expanded = dropdownMenuOpened,
                        onDismissRequest = { dropdownMenuOpened = false }
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(14.dp),
                                    painter = painterResource(Resources.Icon.Delete),
                                    contentDescription = "Delete icon",
                                    tint = IconPrimary
                                )
                            },
                            text = {
                                Text(
                                    text = "Delete",
                                    color = TextPrimary,
                                    fontSize = FontSize.REGULAR
                                )
                            },
                            onClick = {
                                dropdownMenuOpened = false
                                manageProductViewModel.deleteProduct(
                                    onSuccess = navigateBack,
                                    onError = { message -> messageBarState.addError(message) }
                                )
                            }
                        )
                    }
                }
            }
        },
    ) { padding ->
        ContentWithMessageBar(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
            contentBackgroundColor = Surface,
            messageBarState = messageBarState,
            errorMaxLines = 2,
            errorContainerColor = SurfaceError,
            errorContentColor = TextWhite,
            successContainerColor = SurfaceBrand,
            successContentColor = TextPrimary
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(
                        bottom = 24.dp,
                        top = 12.dp
                    )
                    .imePadding()
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(size = 12.dp))
                            .border(
                                width = 1.dp,
                                color = BorderIdle,
                                shape = RoundedCornerShape(size = 12.dp)
                            )
                            .background(SurfaceLighter)
                            .clickable(
                                enabled = thumbnailUploaderState.isIdle()
                            ) {
                                println("Triggered!")
                                photoPicker.open()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        thumbnailUploaderState.DisplayResult(
                            onIdle = {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(Resources.Icon.Plus),
                                    contentDescription = "Plus icon",
                                    tint = IconPrimary
                                )
                            },
                            onLoading = {
                                LoadingCard(modifier = Modifier.fillMaxSize())
                            },
                            onSuccess = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    AsyncImage(
                                        modifier = Modifier.fillMaxSize(),
                                        model = ImageRequest.Builder(
                                            LocalPlatformContext.current
                                        ).data(screenState.thumbnail)
                                            .crossfade(enable = true)
                                            .build(),
                                        contentDescription = "Product thumbnail image",
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(size = 6.dp))
                                            .padding(
                                                top = 12.dp,
                                                end = 12.dp
                                            )
                                            .background(ButtonPrimary)
                                            .clickable {
                                                manageProductViewModel.deleteThumbnailFromStorage(
                                                    onSuccess = { messageBarState.addSuccess("Thumbnail removed successfully.") },
                                                    onError = { message ->
                                                        messageBarState.addError(
                                                            message
                                                        )
                                                    }
                                                )
                                            }
                                            .padding(all = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(14.dp),
                                            painter = painterResource(Resources.Icon.Delete),
                                            contentDescription = "Delete icon"
                                        )
                                    }
                                }
                            },
                            onError = { message ->
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ErrorCard(message = message)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    TextButton(
                                        onClick = {
                                            manageProductViewModel.updateThumbnailUploaderState(RequestState.Idle)
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = "Try again",
                                            fontSize = FontSize.SMALL,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        )
                    }
                    CustomTextField(
                        value = screenState.title,
                        onValueChange = manageProductViewModel::updateTitle,
                        placeholder = "Title"
                    )
                    CustomTextField(
                        modifier = Modifier.height(168.dp),
                        value = screenState.description,
                        onValueChange = manageProductViewModel::updateDescription,
                        placeholder = "Description",
                        expanded = true
                    )
                    AlertTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = screenState.category.title,
                        onClick = { showCategoriesDialog = true }
                    )
                    AnimatedVisibility(
                        visible = screenState.category != ProductCategory.SaladMixed
                    ) {
                        Column {
                            CustomTextField(
                                value = "${screenState.weight ?: ""}",
                                onValueChange = { manageProductViewModel.updateWeight(it.toIntOrNull() ?: 0) },
                                placeholder = "Weight",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = screenState.flavors,
                                onValueChange = manageProductViewModel::updateFlavors,
                                placeholder = "Flavors"
                            )
                        }
                    }
                    CustomTextField(
                        value = "${screenState.price}",
                        onValueChange = { value ->
                            if (value.isEmpty() || value.toDoubleOrNull() != null) {
                                manageProductViewModel.updatePrice(value.toDoubleOrNull() ?: 0.0)
                            }
                        },
                        placeholder = "Price",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = "New",
                                fontSize = FontSize.REGULAR,
                                color = TextPrimary
                            )
                            Switch(
                                checked = screenState.isNew,
                                onCheckedChange = manageProductViewModel::updateNew,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = "Popular",
                                fontSize = FontSize.REGULAR,
                                color = TextPrimary
                            )
                            Switch(
                                checked = screenState.isPopular,
                                onCheckedChange = manageProductViewModel::updatePopular,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = "Discounted",
                                fontSize = FontSize.REGULAR,
                                color = TextPrimary
                            )
                            Switch(
                                checked = screenState.isDiscounted,
                                onCheckedChange = manageProductViewModel::updateDiscounted,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    }
                    }
                }
                SupplrButton(
                    text = if (id == null) "Add new product"
                    else "Update",
                    icon = if (id == null) Resources.Icon.Plus
                    else Resources.Icon.Checkmark,
                    enabled = isFormValid,
                    onClick = {
                        if (id != null) {
                            manageProductViewModel.updateProduct(
                                onSuccess = { messageBarState.addSuccess("Product successfully updated!") },
                                onError = { message -> messageBarState.addError(message) }
                            )
                        } else {
                            manageProductViewModel.createNewProduct(
                                onSuccess = { messageBarState.addSuccess("Product successfully added!") },
                                onError = { message -> messageBarState.addError(message) }
                            )
                        }
                    }
                )
            }
        }
    }
}
