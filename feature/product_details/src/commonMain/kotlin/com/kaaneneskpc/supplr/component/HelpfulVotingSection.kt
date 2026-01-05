package com.kaaneneskpc.supplr.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.supplr.shared.fonts.*

@Composable
fun HelpfulVotingSection(
    helpfulCount: Int,
    unhelpfulCount: Int,
    userVote: Boolean?,
    onHelpfulClick: () -> Unit,
    onUnhelpfulClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Was this helpful?",
            fontFamily = RobotoCondensedFont(),
            fontSize = FontSize.SMALL,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.weight(1f))
        VoteButton(
            isSelected = userVote == true,
            count = helpfulCount,
            isHelpful = true,
            onClick = onHelpfulClick
        )
        VoteButton(
            isSelected = userVote == false,
            count = unhelpfulCount,
            isHelpful = false,
            onClick = onUnhelpfulClick
        )
    }
}

@Composable
private fun VoteButton(
    isSelected: Boolean,
    count: Int,
    isHelpful: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected && isHelpful -> Color(0xFF4CAF50).copy(alpha = 0.2f)
            isSelected && !isHelpful -> SurfaceError.copy(alpha = 0.2f)
            else -> SurfaceLighter
        },
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val iconTint by animateColorAsState(
        targetValue = when {
            isSelected && isHelpful -> Color(0xFF4CAF50)
            isSelected && !isHelpful -> SurfaceError
            else -> TextSecondary
        },
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isHelpful) {
                    if (isSelected) Icons.Filled.KeyboardArrowUp else Icons.Outlined.KeyboardArrowUp
                } else {
                    if (isSelected) Icons.Filled.KeyboardArrowDown else Icons.Outlined.KeyboardArrowDown
                },
                contentDescription = if (isHelpful) "Helpful" else "Not helpful",
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = count.toString(),
                fontFamily = RobotoCondensedFont(),
                fontSize = FontSize.SMALL,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = iconTint
            )
        }
    }
}
