package com.kaaneneskpc.supplr.blog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaaneneskpc.supplr.shared.domain.Article
import com.kaaneneskpc.supplr.shared.domain.ArticleCategory
import com.kaaneneskpc.supplr.shared.fonts.BebasNeueFont
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlue
import com.kaaneneskpc.supplr.shared.fonts.CategoryBlueLighter
import com.kaaneneskpc.supplr.shared.fonts.FontSize
import com.kaaneneskpc.supplr.shared.fonts.IconPrimary
import com.kaaneneskpc.supplr.shared.fonts.Resources
import com.kaaneneskpc.supplr.shared.fonts.Surface
import com.kaaneneskpc.supplr.shared.fonts.SurfaceLighter
import com.kaaneneskpc.supplr.shared.fonts.TextPrimary
import com.kaaneneskpc.supplr.shared.util.RequestState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen(
    navigateBack: () -> Unit,
    navigateToArticle: (String) -> Unit
) {
    val blogViewModel = koinViewModel<BlogViewModel>()
    val articles by blogViewModel.articles.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ArticleCategory?>(null) }
    var showSearch by remember { mutableStateOf(false) }

    // Listen for search query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            blogViewModel.searchArticles(searchQuery)
        } else if (selectedCategory != null) {
            blogViewModel.loadArticlesByCategory(selectedCategory!!)
        } else {
            blogViewModel.loadAllArticles()
        }
    }

    // Listen for category changes
    LaunchedEffect(selectedCategory) {
        if (selectedCategory != null) {
            blogViewModel.loadArticlesByCategory(selectedCategory!!)
        } else if (searchQuery.isBlank()) {
            blogViewModel.loadAllArticles()
        }
    }

    // Get real articles data instead of sample data
    val filteredArticles = when (val articlesState = articles) {
        is RequestState.Success -> {
            val articlesList = articlesState.getSuccessData().filter { it.isPublished }
            if (searchQuery.isNotBlank()) {
                articlesList.filter { article ->
                    article.title.contains(searchQuery, ignoreCase = true) ||
                    article.subtitle.contains(searchQuery, ignoreCase = true) ||
                    article.tags.any { it.contains(searchQuery, ignoreCase = true) }
                }
            } else if (selectedCategory != null) {
                articlesList.filter { it.category == selectedCategory }
            } else {
                articlesList
            }
        }
        else -> emptyList()
    }

    // Keep the old sample articles for fallback
    val sampleArticles = remember {
        listOf(
            Article(
                id = "1",
                title = "Protein İhtiyacınızı Doğru Şekilde Karşılayın",
                subtitle = "Günlük protein ihtiyacınızı hesaplayın ve sağlıklı kaynaklardan karşılayın",
                content = "Protein vücudumuzun temel yapı taşlarından biridir...",
                imageUrl = "",
                author = "Dr. Ayşe Kaya",
                publishDate = "15 Aralık 2024",
                readTimeMinutes = 5,
                category = ArticleCategory.NUTRITION,
                tags = listOf("protein", "beslenme", "sağlık")
            ),
            Article(
                id = "2",
                title = "Evde Yapabileceğiniz 10 Dakikalık HIIT Antrenmanı",
                subtitle = "Zamanınız kısıtlı mı? Bu kısa ama etkili antrenman tam size göre!",
                content = "HIIT (High Intensity Interval Training)...",
                imageUrl = "",
                author = "Antrenör Mehmet Özkan",
                publishDate = "12 Aralık 2024",
                readTimeMinutes = 8,
                category = ArticleCategory.FITNESS,
                tags = listOf("hiit", "antrenman", "evde spor")
            ),
            Article(
                id = "3",
                title = "Sağlıklı Smoothie Bowl Tarifleri",
                subtitle = "Renkli ve besleyici smoothie bowl'lar ile güne enerjik başlayın",
                content = "Smoothie bowl'lar hem gözleri hem de damağı...",
                imageUrl = "",
                author = "Şef Zeynep Aksoy",
                publishDate = "10 Aralık 2024",
                readTimeMinutes = 6,
                category = ArticleCategory.RECIPES,
                tags = listOf("smoothie", "kahvaltı", "sağlıklı tarifler")
            ),
            Article(
                id = "4",
                title = "Stres Yönetimi ve Mental Sağlık",
                subtitle = "Günlük hayatta stresi azaltmanın etkili yolları",
                content = "Modern yaşamın getirdiği stres...",
                imageUrl = "",
                author = "Psikolog Emre Yılmaz",
                publishDate = "8 Aralık 2024",
                readTimeMinutes = 12,
                category = ArticleCategory.WELLNESS,
                tags = listOf("stres", "mental sağlık", "rahatlama")
            )
        )
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Blog",
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
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
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = IconPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Search bar
                AnimatedVisibility(visible = showSearch) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Makale ara...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                // Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CategoryBlueLighter.copy(alpha = 0.3f),
                                        Surface
                                    )
                                )
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(60.dp),
                                shape = CircleShape,
                                color = CategoryBlue.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    painter = painterResource(Resources.Icon.Book),
                                    contentDescription = "Blog",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(12.dp),
                                    tint = CategoryBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Sağlıklı Yaşam Blogu",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Beslenme, spor ve sağlıklı yaşam hakkında uzman yazıları",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Categories
                Text(
                    text = "Kategoriler",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    item {
                        CategoryChip(
                            text = "Tümü",
                            isSelected = selectedCategory == null,
                            onClick = { selectedCategory = null }
                        )
                    }
                    items(ArticleCategory.entries) { category ->
                        CategoryChip(
                            text = category.displayName,
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Son Yazılar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Display articles based on state
            when (val articlesState = articles) {
                is RequestState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CategoryBlue)
                        }
                    }
                }
                is RequestState.Success -> {
                    if (filteredArticles.isNotEmpty()) {
                        items(filteredArticles) { article ->
                            ArticleCard(
                                article = article,
                                onClick = { navigateToArticle(article.id) }
                            )
                        }
                    } else {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(Resources.Icon.Book),
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = TextPrimary.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (searchQuery.isNotBlank() || selectedCategory != null) "Arama kriterlerine uygun makale bulunamadı" else "Henüz makale yok",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = TextPrimary.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                is RequestState.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Makaleler yüklenirken hata oluştu",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = TextPrimary.copy(alpha = 0.7f)
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = articlesState.getErrorMessage(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary.copy(alpha = 0.5f)
                                    )
                                )
                            }
                        }
                    }
                }

                RequestState.Idle -> {

                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) CategoryBlue else SurfaceLighter
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Surface else TextPrimary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ArticleCard(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier,
                    shape = RoundedCornerShape(8.dp),
                    color = CategoryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = article.category.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = CategoryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextPrimary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${article.readTimeMinutes} dk",
                        fontSize = 12.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = article.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary.copy(alpha = 0.7f)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextPrimary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = article.author,
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = article.publishDate,
                    fontSize = 12.sp,
                    color = TextPrimary.copy(alpha = 0.5f)
                )
            }
        }
    }
} 