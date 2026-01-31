package com.kaaneneskpc.supplr.gamification.leaderboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kaaneneskpc.supplr.shared.domain.gamification.LeaderboardEntry
import com.kaaneneskpc.supplr.shared.util.RequestState
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navigateBack: () -> Unit,
    viewModel: LeaderboardViewModel = koinViewModel()
) {
    val leaderboardState by viewModel.leaderboard.collectAsState()
    val userRankState by viewModel.userRank.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🏆 Leaderboard",
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
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refreshLeaderboard()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LaunchedEffect(leaderboardState) {
                if (leaderboardState is RequestState.Success) {
                    delay(500)
                    isRefreshing = false
                }
            }
            when (val state = leaderboardState) {
                is RequestState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is RequestState.Success -> {
                    LeaderboardContent(
                        entries = state.data,
                        userRank = (userRankState as? RequestState.Success)?.data
                    )
                }
                is RequestState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun LeaderboardContent(
    entries: List<LeaderboardEntry>,
    userRank: LeaderboardEntry?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            if (entries.size >= 3) {
                TopThreeSection(entries.take(3))
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if (userRank != null && userRank.rank > 3) {
            item {
                UserRankCard(userRank)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        item {
            Text(
                text = "Top Players",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        itemsIndexed(entries.drop(3)) { index, entry ->
            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(index * 100L)
                isVisible = true
            }
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                LeaderboardEntryCard(entry)
            }
        }
    }
}

@Composable
private fun TopThreeSection(topThree: List<LeaderboardEntry>) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        if (topThree.size >= 2) {
            TopPlayerCard(
                entry = topThree[1],
                medal = "🥈",
                cardHeight = 150.dp,
                avatarSize = 50.dp,
                medalSize = 28.sp,
                isFirst = false,
                modifier = Modifier.weight(0.9f)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        if (topThree.isNotEmpty()) {
            TopPlayerCard(
                entry = topThree[0],
                medal = "👑",
                cardHeight = 200.dp,
                avatarSize = 70.dp,
                medalSize = 36.sp,
                isFirst = true,
                modifier = Modifier.weight(1.2f)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        if (topThree.size >= 3) {
            TopPlayerCard(
                entry = topThree[2],
                medal = "🥉",
                cardHeight = 130.dp,
                avatarSize = 45.dp,
                medalSize = 24.sp,
                isFirst = false,
                modifier = Modifier.weight(0.9f)
            )
        }
    }
}

@Composable
private fun TopPlayerCard(
    entry: LeaderboardEntry,
    medal: String,
    cardHeight: androidx.compose.ui.unit.Dp,
    avatarSize: androidx.compose.ui.unit.Dp,
    medalSize: androidx.compose.ui.unit.TextUnit,
    isFirst: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isFirst) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFD700),
                Color(0xFFFFA500),
                Color(0xFFFF8C00)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            )
        )
    }
    Card(
        modifier = modifier.height(cardHeight),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFirst) 12.dp else 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBackground)
                .padding(if (isFirst) 12.dp else 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = medal,
                    fontSize = medalSize
                )
                Spacer(modifier = Modifier.height(if (isFirst) 8.dp else 4.dp))
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(if (isFirst) Color.White else MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    if (entry.profilePhotoUrl != null) {
                        AsyncImage(
                            model = entry.profilePhotoUrl,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(avatarSize / 2),
                            tint = if (isFirst) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(if (isFirst) 8.dp else 4.dp))
                Text(
                    text = entry.userName,
                    style = if (isFirst) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = if (isFirst) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$${entry.totalSpent.toLong()}",
                    style = if (isFirst) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
                    color = if (isFirst) Color.White else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                if (isFirst) {
                    Text(
                        text = "${entry.totalOrders} orders",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardEntryCard(entry: LeaderboardEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(40.dp)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (entry.profilePhotoUrl != null) {
                    AsyncImage(
                        model = entry.profilePhotoUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.userName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${entry.totalOrders} orders",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$${entry.totalSpent.toLong()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun UserRankCard(userRank: LeaderboardEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Rank: #${userRank.rank}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${userRank.totalSpent.toLong()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
