package com.kaaneneskpc.supplr.blog

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun ArticleDetailScreen(
    articleId: String,
    navigateBack: () -> Unit
) {
    val blogViewModel = koinViewModel<BlogViewModel>()
    val selectedArticle by blogViewModel.selectedArticle.collectAsState()
    
    // Load article when screen is created
    LaunchedEffect(articleId) {
        blogViewModel.loadArticleById(articleId)
    }
    
    // Fallback sample article data
    val fallbackArticle = remember {
        when (articleId) {
            "1" -> Article(
                id = "1",
                title = "Protein İhtiyacınızı Doğru Şekilde Karşılayın",
                subtitle = "Günlük protein ihtiyacınızı hesaplayın ve sağlıklı kaynaklardan karşılayın",
                content = """
                    Protein vücudumuzun temel yapı taşlarından biridir ve günlük beslenmemizde kritik bir rol oynar. Kas gelişimi, doku onarımı ve bağışıklık sisteminin güçlendirilmesi için yeterli protein alımı şarttır.
                    
                    ## Günlük Protein İhtiyacı
                    
                    Genel olarak, sedanter bir yetişkin için önerilen günlük protein miktarı vücut ağırlığının kilogram başına 0.8-1 gramdır. Ancak bu miktar aktivite seviyenize göre değişebilir:
                    
                    • **Sedanter yaşam:** 0.8g/kg
                    • **Hafif aktif:** 1.0-1.2g/kg  
                    • **Orta düzey aktif:** 1.2-1.6g/kg
                    • **Yoğun antrenman:** 1.6-2.2g/kg
                    
                    ## Kaliteli Protein Kaynakları
                    
                    **Hayvansal Kaynaklar:**
                    • Tavuk göğsü
                    • Balık (somon, ton balığı)
                    • Yumurta
                    • Süt ürünleri (yoğurt, peynir)
                    
                    **Bitkisel Kaynaklar:**
                    • Baklagiller (mercimek, nohut, fasulye)
                    • Quinoa
                    • Chia tohumu
                    • Fındık ve fıstık
                    
                    ## Protein Alımında Dikkat Edilecekler
                    
                    1. **Dağılım:** Proteini gün boyunca eşit olarak dağıtın
                    2. **Kalite:** Tam protein içeren besinleri tercih edin
                    3. **Zamanlama:** Antrenman sonrası 30-60 dakika içinde protein alın
                    4. **Hidrasyon:** Protein alımını artırdığınızda su tüketiminizi de artırın
                    
                    Protein ihtiyacınızı karşılarken çeşitliliğe dikkat edin ve vücudunuzun ihtiyaçlarını dinleyin. Beslenme uzmanından destek almak her zaman faydalı olacaktır.
                """.trimIndent(),
                imageUrl = "",
                author = "Dr. Ayşe Kaya",
                publishDate = "15 Aralık 2024",
                readTimeMinutes = 5,
                category = ArticleCategory.NUTRITION,
                tags = listOf("protein", "beslenme", "sağlık")
            )
            "2" -> Article(
                id = "2",
                title = "Evde Yapabileceğiniz 10 Dakikalık HIIT Antrenmanı",
                subtitle = "Zamanınız kısıtlı mı? Bu kısa ama etkili antrenman tam size göre!",
                content = """
                    HIIT (High Intensity Interval Training), kısa süreli yoğun egzersizler ve dinlenme periyotlarının bir araya geldiği etkili bir antrenman yöntemidir. Sadece 10 dakikada harika sonuçlar elde edebilirsiniz!
                    
                    ## HIIT'in Faydaları
                    
                    • **Zaman tasarrufu:** Kısa sürede maksimum fayda
                    • **Yağ yakımı:** Antrenman sonrası da kalori yakımı devam eder
                    • **Kardiyovasküler sağlık:** Kalp ve dolaşım sistemini güçlendirir
                    • **Metabolizma:** Bazal metabolizma hızını artırır
                    
                    ## 10 Dakikalık HIIT Programı
                    
                    **Isınma (2 dakika)**
                    • Yerinde yürüyüş - 30 saniye
                    • Kol çevirme - 30 saniye
                    • Hafif zıplama - 30 saniye
                    • Diz kaldırma - 30 saniye
                    
                    **Ana Antrenman (6 dakika)**
                    Her egzersizi 45 saniye yap, 15 saniye dinlen:
                    
                    1. **Burpees** - Tam vücut egzersizi
                    2. **Squat Jump** - Alt vücut gücü
                    3. **Mountain Climbers** - Karın ve kardiyovasküler
                    4. **Push-up** - Üst vücut gücü
                    5. **High Knees** - Kardiyovasküler dayanıklılık
                    6. **Plank Jacks** - Core stabilite
                    
                    **Soğuma (2 dakika)**
                    • Yavaş yürüyüş - 1 dakika
                    • Esnetme hareketleri - 1 dakika
                    
                    ## İpuçları
                    
                    • Hareketleri doğru form ile yapın
                    • Kendi hızınızda ilerleyin
                    • Düzenli su için
                    • Haftada 3-4 kez tekrarlayın
                    
                    Bu antrenman programı ile zamanınızı verimli kullanarak fitness hedeflerinize ulaşabilirsiniz!
                """.trimIndent(),
                imageUrl = "",
                author = "Antrenör Mehmet Özkan",
                publishDate = "12 Aralık 2024",
                readTimeMinutes = 8,
                category = ArticleCategory.FITNESS,
                tags = listOf("hiit", "antrenman", "evde spor")
            )
            else -> null
        }
    }

    // Get article from state or fallback
    val article = when (val articleState = selectedArticle) {
        is RequestState.Success -> articleState.getSuccessData()
        else -> fallbackArticle
    }

    when (val articleState = selectedArticle) {
        is RequestState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CategoryBlue)
            }
            return
        }
        is RequestState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Makale yüklenirken hata oluştu")
                    Text(
                        text = articleState.getErrorMessage(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            return
        }
        is RequestState.Success -> {
            if (articleState.getSuccessData() == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Makale bulunamadı")
                }
                return
            }
        }

        RequestState.Idle -> TODO()
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Makale",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = TextPrimary
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
                // Article Header Card
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
                            .padding(24.dp)
                    ) {
                        Column {
                            // Category badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CategoryBlue.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = article?.category?.displayName.orEmpty(),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = CategoryBlue,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Title
                            Text(
                                text = article?.title.orEmpty(),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Subtitle
                            Text(
                                text = article?.subtitle.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextPrimary.copy(alpha = 0.7f)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Article meta info
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
                                        modifier = Modifier.size(18.dp),
                                        tint = CategoryBlue
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = article?.author.orEmpty(),
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = TextPrimary.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = article?.publishDate.orEmpty(),
                                        fontSize = 12.sp,
                                        color = TextPrimary.copy(alpha = 0.6f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = TextPrimary.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${article?.readTimeMinutes} dk",
                                        fontSize = 12.sp,
                                        color = TextPrimary.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Article Content
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = article?.content.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            item {
                // Author Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Yazar Hakkında",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SurfaceLighter)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = CategoryBlue.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(12.dp),
                                    tint = CategoryBlue
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = article?.author.orEmpty(),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = when (article?.category) {
                                        ArticleCategory.NUTRITION -> "Beslenme Uzmanı"
                                        ArticleCategory.FITNESS -> "Fitness Antrenörü"
                                        ArticleCategory.WELLNESS -> "Wellness Uzmanı"
                                        ArticleCategory.RECIPES -> "Şef"
                                        ArticleCategory.LIFESTYLE -> "Yaşam Koçu"
                                        null -> TODO()
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Tags
                if (article?.tags?.isNotEmpty() == true) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Etiketler",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                article?.tags?.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = CategoryBlue.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            color = CategoryBlue,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 