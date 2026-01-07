package com.kaaneneskpc.supplr.checkout.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.domain.Coupon
import com.kaaneneskpc.supplr.shared.domain.CouponType
import com.kaaneneskpc.supplr.shared.fonts.Alpha
import com.kaaneneskpc.supplr.shared.fonts.BorderError
import com.kaaneneskpc.supplr.shared.fonts.BorderIdle
import com.kaaneneskpc.supplr.shared.fonts.ButtonPrimary
import com.kaaneneskpc.supplr.shared.fonts.CategoryGreen
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.SurfaceBrand
import com.kaaneneskpc.supplr.shared.fonts.SurfaceError
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.util.formatPrice
import org.jetbrains.compose.resources.painterResource

@Composable
fun CouponInputSection(
    couponCode: String,
    onCouponCodeChange: (String) -> Unit,
    appliedCoupon: Coupon?,
    couponDiscount: Double,
    isValidating: Boolean,
    error: String?,
    onApplyCoupon: () -> Unit,
    onRemoveCoupon: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Coupon Code",
            fontSize = FontSize.REGULAR,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = if (error != null) BorderError else BorderIdle,
                        shape = RoundedCornerShape(size = 6.dp)
                    )
                    .clip(RoundedCornerShape(size = 6.dp)),
                value = couponCode,
                onValueChange = onCouponCodeChange,
                enabled = appliedCoupon == null && !isValidating,
                placeholder = {
                    Text(
                        text = "Enter coupon code",
                        fontSize = FontSize.REGULAR,
                        color = TextPrimary.copy(alpha = Alpha.HALF)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Characters
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = SurfaceLighter,
                    focusedContainerColor = SurfaceLighter,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    disabledTextColor = TextPrimary.copy(alpha = Alpha.DISABLED),
                    disabledContainerColor = SurfaceLighter,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            if (appliedCoupon != null) {
                IconButton(
                    onClick = onRemoveCoupon,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = SurfaceError.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(Resources.Icon.Delete),
                        contentDescription = "Remove coupon",
                        tint = SurfaceError,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Button(
                    onClick = onApplyCoupon,
                    enabled = couponCode.isNotBlank() && !isValidating,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonPrimary,
                        contentColor = TextPrimary,
                        disabledContainerColor = ButtonPrimary.copy(alpha = Alpha.DISABLED),
                        disabledContentColor = TextPrimary.copy(alpha = Alpha.DISABLED)
                    ),
                    modifier = Modifier.height(56.dp)
                ) {
                    if (isValidating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TextPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Apply",
                            fontSize = FontSize.REGULAR,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            error?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    fontSize = FontSize.SMALL,
                    color = SurfaceError
                )
            }
        }
        AnimatedVisibility(
            visible = appliedCoupon != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            appliedCoupon?.let { coupon ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = SurfaceBrand.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Coupon Applied!",
                            fontSize = FontSize.REGULAR,
                            fontWeight = FontWeight.SemiBold,
                            color = CategoryGreen
                        )
                        Text(
                            text = formatCouponDescription(coupon),
                            fontSize = FontSize.SMALL,
                            color = TextPrimary.copy(alpha = Alpha.HALF)
                        )
                    }
                    Text(
                        text = "-$${formatPrice(couponDiscount)}",
                        fontSize = FontSize.MEDIUM,
                        fontWeight = FontWeight.Bold,
                        color =  CategoryGreen
                    )
                }
            }
        }
    }
}

private fun formatCouponDescription(coupon: Coupon): String {
    return when (coupon.type) {
        CouponType.PERCENTAGE -> "${coupon.value.toInt()}% off"
        CouponType.FIXED_AMOUNT -> "$${coupon.value} off"
        CouponType.FREE_SHIPPING -> "Free shipping"
    }
}
