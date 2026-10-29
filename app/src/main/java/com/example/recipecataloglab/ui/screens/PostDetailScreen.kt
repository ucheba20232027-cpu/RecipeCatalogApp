package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipecataloglab.data.Post
import com.example.recipecataloglab.data.PostsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(postId: Int, onBack: () -> Unit) {
    var post by remember { mutableStateOf<Post?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(postId) {
        isLoading = true
        PostsRepository().getPost(postId)
            .onSuccess { post = it; error = null }
            .onFailure { error = it.message }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пост #$postId") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Назад") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
                post != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(post!!.title, fontSize = 22.sp, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(12.dp))
                        Text(post!!.body, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}