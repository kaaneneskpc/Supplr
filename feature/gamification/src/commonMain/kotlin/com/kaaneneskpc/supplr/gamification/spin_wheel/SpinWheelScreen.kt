package com.kaaneneskpc.supplr.gamification.spin_wheel

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.domain.gamification.SpinWheelPrize
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

private val DEFAULT_WHEEL_COLORS = listOf(
    0xFFE91E63L,
    0xFF9C27B0L,
    0xFF3F51B5L,
    0xFF03A9F4L,
    0xFF4CAF50L,
    0xFFFFEB3BL,
    0xFFFF9800L,
    0xFFF44336L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinWheelScreen(
    navigateBack: () -> Unit,
    viewModel: SpinWheelViewModel = koinViewModel()
) {
    val prizesState by viewModel.prizes.collectAsState()
    val canSpinState by viewModel.canSpin.collectAsState()
    val spinState by viewModel.spinState.collectAsState()
    val message by viewModel.message.collectAsState()
    var showResultDialog by remember { mutableStateOf(false) }
    LaunchedEffect(spinState) {
        if (spinState is SpinState.Result) {
            showResultDialog = true
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🎰 Spin Wheel",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            when (val state = prizesState) {
                is RequestState.Loading -> {
                    CircularProgressIndicator()
                }
                is RequestState.Success -> {
                    SpinWheelContent(
                        prizes = state.data.ifEmpty { getDefaultPrizes() },
                        canSpin = (canSpinState as? RequestState.Success)?.data ?: true,
                        spinState = spinState,
                        onSpin = { prizeIndex ->
                            val prizes = state.data.ifEmpty { getDefaultPrizes() }
                            viewModel.startSpin(prizeIndex, prizes)
                        }
                    )
                }
                is RequestState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
        if (showResultDialog && spinState is SpinState.Result) {
            PrizeResultDialog(
                prize = (spinState as SpinState.Result).prize,
                onDismiss = {
                    showResultDialog = false
                    viewModel.resetSpin()
                }
            )
        }
    }
}

@Composable
private fun SpinWheelContent(
    prizes: List<SpinWheelPrize>,
    canSpin: Boolean,
    spinState: SpinState,
    onSpin: (Int) -> Unit
) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var targetPrizeIndex by remember { mutableStateOf(0) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "Spin to Win!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (canSpin) "You have 1 free spin today!" else "Come back tomorrow for another spin!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(300.dp)
            ) {
                val sweepAngle = 360f / prizes.size
                prizes.forEachIndexed { index, prize ->
                    val startAngle = index * sweepAngle + rotation.value
                    val color = if (prize.color != 0L) {
                        Color(prize.color)
                    } else {
                        Color(DEFAULT_WHEEL_COLORS[index % DEFAULT_WHEEL_COLORS.size])
                    }
                    rotate(rotation.value) {
                        drawArc(
                            color = color,
                            startAngle = index * sweepAngle - 90,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = size
                        )
                        drawArc(
                            color = Color.White.copy(alpha = 0.3f),
                            startAngle = index * sweepAngle - 90,
                            sweepAngle = 1f,
                            useCenter = true,
                            size = size
                        )
                    }
                }
                drawCircle(
                    color = Color.White,
                    radius = 30.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color(0xFFFFD700),
                    radius = 25.dp.toPx(),
                    center = center
                )
            }
            val pointerPath = remember {
                Path().apply {
                    moveTo(0f, -160f)
                    lineTo(-15f, -130f)
                    lineTo(15f, -130f)
                    close()
                }
            }
            Canvas(modifier = Modifier.size(300.dp)) {
                drawPath(
                    path = Path().apply {
                        moveTo(center.x, center.y - 145.dp.toPx())
                        lineTo(center.x - 12.dp.toPx(), center.y - 125.dp.toPx())
                        lineTo(center.x + 12.dp.toPx(), center.y - 125.dp.toPx())
                        close()
                    },
                    color = Color(0xFFFFD700)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (canSpin && spinState is SpinState.Idle) {
                    targetPrizeIndex = Random.nextInt(prizes.size)
                    scope.launch {
                        val targetRotation = 360f * 5 + (360f - targetPrizeIndex * (360f / prizes.size))
                        rotation.animateTo(
                            targetValue = targetRotation,
                            animationSpec = tween(
                                durationMillis = 3000,
                                easing = FastOutSlowInEasing
                            )
                        )
                        onSpin(targetPrizeIndex)
                    }
                }
            },
            enabled = canSpin && spinState is SpinState.Idle,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = when {
                    spinState is SpinState.Spinning -> "Spinning..."
                    !canSpin -> "No Spins Left Today"
                    else -> "🎰 SPIN NOW!"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PrizeResultDialog(
    prize: SpinWheelPrize,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Awesome!")
            }
        },
        title = {
            Text(
                text = "🎉 Congratulations!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "You won:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = prize.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                if (prize.couponCode != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Coupon Code: ${prize.couponCode}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}

private fun getDefaultPrizes(): List<SpinWheelPrize> = listOf(
    SpinWheelPrize("1", "10% Off", com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType.DISCOUNT_PERCENTAGE, 10.0, "SPIN10", 0xFFE91E63L),
    SpinWheelPrize("2", "Free Shipping", com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType.FREE_SHIPPING, 0.0, "FREESHIP", 0xFF9C27B0L),
    SpinWheelPrize("3", "$5 Off", com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType.DISCOUNT_FIXED, 5.0, "SAVE5", 0xFF3F51B5L),
    SpinWheelPrize("4", "50 Points", com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType.POINTS, 50.0, null, 0xFF03A9F4L),
    SpinWheelPrize("5", "20% Off", com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType.DISCOUNT_PERCENTAGE, 20.0, "SPIN20", 0xFF4CAF50L),
    SpinWheelPrize("6", "Try Again", com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType.EMPTY, 0.0, null, 0xFFFFEB3BL),
    SpinWheelPrize("7", "$10 Off", com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType.DISCOUNT_FIXED, 10.0, "SAVE10", 0xFFFF9800L),
    SpinWheelPrize("8", "100 Points", com.kaaneneskpc.supplr.shared.domain.gamification.PrizeType.POINTS, 100.0, null, 0xFFF44336L)
)
