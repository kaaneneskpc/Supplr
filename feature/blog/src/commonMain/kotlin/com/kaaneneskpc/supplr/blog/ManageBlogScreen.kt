package com.kaaneneskpc.supplr.blog

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaaneneskpc.supplr.shared.component.SupplrButton
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
import com.kaaneneskpc.supplr.shared.fonts.White
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBlogScreen(
    articleId: String? = null,
    navigateBack: () -> Unit
) {
    val blogViewModel = koinViewModel<BlogViewModel>()
    val formState = blogViewModel.articleFormState
    var tagInput by remember { mutableStateOf("") }
    var showImagePicker by remember { mutableStateOf(false) }
    
    val photoPicker = remember { PhotoPickerBlog() }
    val isEditing = articleId != null

    // Load article for editing if articleId is provided
    if (isEditing && articleId != null) {
        val articles by blogViewModel.articles.collectAsState()
        articles.getSuccessData()?.find { it.id == articleId }?.let { article ->
            blogViewModel.loadArticleForEditing(article)
        }
    }

    photoPicker.InitializePhotoPicker { file ->
        file?.let { imageFile ->
            if (isEditing && articleId != null) {
                blogViewModel.uploadImage(
                    articleId = articleId,
                    imageFile = imageFile,
                    onSuccess = { },
                    onError = { }
                )
            } else {
                // For new articles, we'll upload after creating the article
                // For now, just store the file reference
            }
        }
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Article" else "Create Article",
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
                                    painter = painterResource(Resources.Icon.Edit),
                                    contentDescription = "Create Article",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(12.dp),
                                    tint = CategoryBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isEditing) "Edit Blog Article" else "Create New Article",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Fill in the details below to ${if (isEditing) "update" else "create"} your article",
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
                // Basic Information
                FormSection(title = "Basic Information") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = formState.title,
                            onValueChange = blogViewModel::updateTitle,
                            label = { Text("Article Title") },
                            placeholder = { Text("Enter article title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = formState.subtitle,
                            onValueChange = blogViewModel::updateSubtitle,
                            label = { Text("Subtitle") },
                            placeholder = { Text("Enter article subtitle") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = formState.content,
                            onValueChange = blogViewModel::updateContent,
                            label = { Text("Article Content") },
                            placeholder = { Text("Write your article content here...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 10
                        )
                    }
                }
            }

            item {
                // Author Information
                FormSection(title = "Author Information") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = formState.author,
                            onValueChange = blogViewModel::updateAuthor,
                            label = { Text("Author Name") },
                            placeholder = { Text("Enter author name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = formState.authorTitle,
                            onValueChange = blogViewModel::updateAuthorTitle,
                            label = { Text("Author Title") },
                            placeholder = { Text("e.g., Nutritionist, Fitness Trainer") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                // Category Selection
                FormSection(title = "Category & Tags") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Select Category",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ArticleCategory.entries) { category ->
                                CategoryChip(
                                    text = category.displayName,
                                    isSelected = formState.category == category,
                                    onClick = { blogViewModel.updateCategory(category) }
                                )
                            }
                        }

                        // Tags input
                        OutlinedTextField(
                            value = tagInput,
                            onValueChange = { tagInput = it },
                            label = { Text("Add Tags") },
                            placeholder = { Text("Enter tag and press Add") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (tagInput.isNotBlank()) {
                                            val newTags = formState.tags + tagInput.trim()
                                            blogViewModel.updateTags(newTags)
                                            tagInput = ""
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Tag")
                                }
                            }
                        )

                        // Display current tags
                        if (formState.tags.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(formState.tags) { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = CategoryBlue.copy(alpha = 0.1f),
                                        modifier = Modifier.clickable {
                                            // Remove tag on click
                                            val newTags = formState.tags - tag
                                            blogViewModel.updateTags(newTags)
                                        }
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

            item {
                // Image Upload
                FormSection(title = "Article Image") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (formState.imageUrl.isNotBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                // Here you would display the image
                                // For now, just show a placeholder
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(SurfaceLighter),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Image Uploaded Successfully")
                                }
                            }
                        }

                        Button(
                            onClick = { photoPicker.open() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (formState.imageUrl.isNotBlank()) "Change Image" else "Upload Image")
                        }
                    }
                }
            }

            item {
                // Publishing Options
                FormSection(title = "Publishing Options") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Publish Article",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Make this article visible to users",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary.copy(alpha = 0.7f)
                                )
                            )
                        }
                        Switch(
                            checked = formState.isPublished,
                            onCheckedChange = blogViewModel::updateIsPublished
                        )
                    }
                }
            }

            item {
                // Action Buttons
                Spacer(modifier = Modifier.height(16.dp))
                SupplrButton(
                    text = if (isEditing) "Update Article" else "Create Article",
                    icon = Resources.Icon.Checkmark,
                    enabled = blogViewModel.isFormValid(),
                    onClick = {
                        if (isEditing && articleId != null) {
                            blogViewModel.updateArticle(
                                articleId = articleId,
                                onSuccess = {
                                    navigateBack()
                                },
                                onError = { /* Handle error */ }
                            )
                        } else {
                            blogViewModel.createArticle(
                                onSuccess = {
                                    navigateBack()
                                },
                                onError = { /* Handle error */ }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable () -> Unit
) {
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
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
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
            color = if (isSelected) White else TextPrimary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
} 