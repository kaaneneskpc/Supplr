package com.kaaneneskpc.supplr.admin_panel

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.component.InfoCard
import com.kaaneneskpc.supplr.shared.component.LoadingCard
import com.kaaneneskpc.supplr.shared.domain.Coupon
import com.kaaneneskpc.supplr.shared.domain.CouponType
import com.kaaneneskpc.supplr.shared.fonts.Alpha
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.BorderIdle
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.CategoryGreen
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
import com.kaaneneskpc.supplr.shared.util.formatDate
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCouponsScreen(
    navigateBack: () -> Unit
) {
    val viewModel = koinViewModel<AdminCouponsViewModel>()
    val coupons by viewModel.coupons.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Coupons",
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back",
                            tint = IconPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showCreateForm,
                containerColor = ButtonPrimary,
                contentColor = IconPrimary
            ) {
                Icon(
                    painter = painterResource(Resources.Icon.Plus),
                    contentDescription = "Add Coupon"
                )
            }
        }
    ) { padding ->
        coupons.DisplayResult(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            ),
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { couponList ->
                if (couponList.isEmpty()) {
                    InfoCard(
                        image = Resources.Image.Cat,
                        title = "No Coupons",
                        subtitle = "Tap + to create your first coupon"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(couponList, key = { it.id }) { coupon ->
                            CouponCard(
                                coupon = coupon,
                                onEdit = { viewModel.showEditForm(coupon) },
                                onDelete = { viewModel.deleteCoupon(coupon.id) }
                            )
                        }
                    }
                }
            },
            onError = { message ->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Error",
                    subtitle = message
                )
            }
        )
    }
    if (viewModel.isFormVisible) {
        ModalBottomSheet(
            onDismissRequest = viewModel::hideForm,
            sheetState = sheetState,
            containerColor = Surface
        ) {
            CouponForm(
                formState = viewModel.formState,
                isSaving = viewModel.isSaving,
                onCodeChange = viewModel::updateCode,
                onTypeChange = viewModel::updateType,
                onValueChange = viewModel::updateValue,
                onMinimumOrderAmountChange = viewModel::updateMinimumOrderAmount,
                onMaximumDiscountChange = viewModel::updateMaximumDiscount,
                onUsageLimitChange = viewModel::updateUsageLimit,
                onExpirationDaysChange = viewModel::updateExpirationDays,
                onIsActiveChange = viewModel::updateIsActive,
                onSave = viewModel::saveCoupon,
                onCancel = viewModel::hideForm
            )
        }
    }
    LaunchedEffect(viewModel.errorMessage, viewModel.successMessage) {
        if (viewModel.errorMessage != null || viewModel.successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }
}

@Composable
private fun CouponCard(
    coupon: Coupon,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLighter),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = coupon.code,
                        fontSize = FontSize.MEDIUM,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (coupon.isActive) CategoryGreen.copy(alpha = 0.2f) else SurfaceError.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (coupon.isActive) "Active" else "Inactive",
                            fontSize = FontSize.SMALL,
                            color = if (coupon.isActive) Surface else SurfaceError
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(Resources.Icon.Edit),
                            contentDescription = "Edit",
                            tint = IconPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(Resources.Icon.Delete),
                            contentDescription = "Delete",
                            tint = SurfaceError,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderIdle)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Discount",
                        fontSize = FontSize.SMALL,
                        color = TextPrimary.copy(alpha = Alpha.HALF)
                    )
                    Text(
                        text = formatDiscount(coupon),
                        fontSize = FontSize.REGULAR,
                        fontWeight = FontWeight.Medium,
                        color = CategoryGreen
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Usage",
                        fontSize = FontSize.SMALL,
                        color = TextPrimary.copy(alpha = Alpha.HALF)
                    )
                    Text(
                        text = "${coupon.usageCount}${coupon.usageLimit?.let { "/$it" } ?: ""}",
                        fontSize = FontSize.REGULAR,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Min. Order",
                        fontSize = FontSize.SMALL,
                        color = TextPrimary.copy(alpha = Alpha.HALF)
                    )
                    Text(
                        text = if (coupon.minimumOrderAmount > 0) "$${coupon.minimumOrderAmount}" else "None",
                        fontSize = FontSize.REGULAR,
                        color = TextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Expires",
                        fontSize = FontSize.SMALL,
                        color = TextPrimary.copy(alpha = Alpha.HALF)
                    )
                    Text(
                        text = formatDate(coupon.expirationDate),
                        fontSize = FontSize.REGULAR,
                        color = if (coupon.isExpired()) SurfaceError else TextPrimary
                    )
                }
            }
        }
    }
    if (showDeleteConfirm) {
        DeleteConfirmDialog(
            couponCode = coupon.code,
            onConfirm = {
                onDelete()
                showDeleteConfirm = false
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}

@Composable
private fun DeleteConfirmDialog(
    couponCode: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface.copy(alpha = 0.8f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .clickable(enabled = false) { },
            colors = CardDefaults.cardColors(containerColor = SurfaceLighter)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delete Coupon?",
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Are you sure you want to delete '$couponCode'?",
                    fontSize = FontSize.REGULAR,
                    color = TextPrimary.copy(alpha = Alpha.HALF)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceLighter)
                    ) {
                        Text("Cancel", color = TextPrimary)
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceError)
                    ) {
                        Text("Delete", color = TextWhite)
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponForm(
    formState: CouponFormState,
    isSaving: Boolean,
    onCodeChange: (String) -> Unit,
    onTypeChange: (CouponType) -> Unit,
    onValueChange: (String) -> Unit,
    onMinimumOrderAmountChange: (String) -> Unit,
    onMaximumDiscountChange: (String) -> Unit,
    onUsageLimitChange: (String) -> Unit,
    onExpirationDaysChange: (String) -> Unit,
    onIsActiveChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var typeExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (formState.isEditing) "Edit Coupon" else "Create Coupon",
            fontSize = FontSize.LARGE,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(20.dp))
        FormTextField(
            label = "Coupon Code",
            value = formState.code,
            onValueChange = onCodeChange,
            placeholder = "e.g., SAVE10"
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Discount Type",
            fontSize = FontSize.SMALL,
            color = TextPrimary.copy(alpha = Alpha.HALF)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            OutlinedTextField(
                value = formState.type.name.replace("_", " "),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { typeExpanded = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BorderIdle,
                    unfocusedBorderColor = BorderIdle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
            DropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                CouponType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name.replace("_", " ")) },
                        onClick = {
                            onTypeChange(type)
                            typeExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        FormTextField(
            label = if (formState.type == CouponType.PERCENTAGE) "Discount (%)" else "Discount Amount ($)",
            value = formState.value,
            onValueChange = onValueChange,
            placeholder = if (formState.type == CouponType.PERCENTAGE) "e.g., 10" else "e.g., 5.00",
            keyboardType = KeyboardType.Decimal
        )
        Spacer(modifier = Modifier.height(12.dp))
        FormTextField(
            label = "Minimum Order Amount ($)",
            value = formState.minimumOrderAmount,
            onValueChange = onMinimumOrderAmountChange,
            placeholder = "e.g., 50.00",
            keyboardType = KeyboardType.Decimal
        )
        if (formState.type == CouponType.PERCENTAGE) {
            Spacer(modifier = Modifier.height(12.dp))
            FormTextField(
                label = "Maximum Discount ($)",
                value = formState.maximumDiscount,
                onValueChange = onMaximumDiscountChange,
                placeholder = "e.g., 25.00 (optional)",
                keyboardType = KeyboardType.Decimal
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        FormTextField(
            label = "Usage Limit",
            value = formState.usageLimit,
            onValueChange = onUsageLimitChange,
            placeholder = "e.g., 100 (optional)",
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(12.dp))
        FormTextField(
            label = "Expires in (days)",
            value = formState.expirationDays,
            onValueChange = onExpirationDaysChange,
            placeholder = "e.g., 30",
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active",
                fontSize = FontSize.REGULAR,
                color = TextPrimary
            )
            Switch(
                checked = formState.isActive,
                onCheckedChange = onIsActiveChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextWhite,
                    checkedTrackColor = SurfaceBrand,
                    uncheckedThumbColor = TextPrimary,
                    uncheckedTrackColor = SurfaceLighter
                )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceLighter)
            ) {
                Text("Cancel", color = TextPrimary)
            }
            Button(
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (formState.isEditing) "Update" else "Create",
                        color = TextPrimary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontSize = FontSize.SMALL,
            color = TextPrimary.copy(alpha = Alpha.HALF)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextPrimary.copy(alpha = Alpha.DISABLED)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SurfaceBrand,
                unfocusedBorderColor = BorderIdle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )
    }
}

private fun formatDiscount(coupon: Coupon): String {
    return when (coupon.type) {
        CouponType.PERCENTAGE -> "${coupon.value.toInt()}% off"
        CouponType.FIXED_AMOUNT -> "$${coupon.value} off"
        CouponType.FREE_SHIPPING -> "Free Shipping"
    }
}
