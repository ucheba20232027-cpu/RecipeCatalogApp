package com.example.recipecataloglab.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipecataloglab.data.Recipe
import com.example.recipecataloglab.data.RecipeJson
import com.example.recipecataloglab.data.RecipeRepository
import com.example.recipecataloglab.ui.components.RecipeCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onRecipeClick: (Int) -> Unit,
    onBack: () -> Unit,
    onAddRecipe: () -> Unit,
    viewModel: RecipesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val allRecipes by RecipeRepository.all.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Все") }
    var showMenu by remember { mutableStateOf(false) }

    // --- ЛОГИКА ЭКСПОРТА ---
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val jsonString = RecipeJson.encode(allRecipes)
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(jsonString.toByteArray())
                    }
                    Toast.makeText(context, "Рецепты успешно экспортированы", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Ошибка экспорта: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- ЛОГИКА ИМПОРТА ---
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    if (jsonString != null) {
                        val importedRecipes = RecipeJson.decode(jsonString)
                        RecipeRepository.replaceAll(importedRecipes)
                        Toast.makeText(context, "Загружено рецептов: ${importedRecipes.size}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Ошибка импорта: неверный формат файла", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // МОДУЛЬ 2. Загрузка — ровно один раз
    LaunchedEffect(Unit) {
        viewModel.loadRecipes(forceRefresh = true)
    }

    // МОДУЛЬ 2. Дебаунс поиска
    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            try {
                delay(300)
                viewModel.searchRemote(query)
            } finally {
                Log.d("DEBOUNCE", "cancel/finish: $query")
            }
        }
    }

    val categories = remember(allRecipes) {
        listOf("Все") + allRecipes.map { it.category }.distinct().sorted()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Рецепты")
                        AnimatedVisibility(visible = isSaving, enter = fadeIn(), exit = fadeOut()) {
                            SavingIndicator()
                        }
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Назад") }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Экспорт в файл (JSON)") },
                            onClick = {
                                showMenu = false
                                exportLauncher.launch("my_recipes.json")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Импорт из файла") },
                            onClick = {
                                showMenu = false
                                importLauncher.launch(arrayOf("application/json"))
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState is RecipesUiState.Success) {
                    SmallFloatingActionButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }
                FloatingActionButton(
                    onClick = {
                        viewModel.persistChanges()
                        onAddRecipe()
                    },
                    modifier = Modifier.testTag("addRecipeFAB")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить рецепт")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is RecipesUiState.Loading -> LoadingState()
                is RecipesUiState.Error -> ErrorState(message = state.message, onRetry = { viewModel.retry() })
                is RecipesUiState.Success -> ContentState(
                    recipes = allRecipes,
                    categories = categories,
                    query = query,
                    onQueryChange = { query = it },
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    onRecipeClick = onRecipeClick,
                    isRefreshing = false
                )
                is RecipesUiState.Refreshing -> ContentState(
                    recipes = allRecipes,
                    categories = categories,
                    query = query,
                    onQueryChange = { query = it },
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    onRecipeClick = onRecipeClick,
                    isRefreshing = true
                )
            }
        }
    }
}

@Composable
private fun SavingIndicator() {
    val transition = rememberInfiniteTransition(label = "savingDots")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600), repeatMode = RepeatMode.Reverse),
        label = "savingAlpha"
    )
    Text(
        text = " · Сохранение...",
        modifier = Modifier.padding(start = 8.dp).alpha(alpha),
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ContentState(
    recipes: List<Recipe>,
    categories: List<String>,
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    onRecipeClick: (Int) -> Unit,
    isRefreshing: Boolean
) {
    val filtered by remember(recipes, query, selectedCategory) {
        derivedStateOf {
            recipes.filter { recipe ->
                val matchesCategory = selectedCategory == "Все" || recipe.category == selectedCategory
                val matchesQuery = recipe.title.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Поиск рецепта") },
            placeholder = { Text("Например: борщ") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("searchField")
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategoryChange(category) },
                    label = { Text(category) }
                )
            }
        }

        Text(
            "Найдено: ${filtered.size} из ${recipes.size}",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (filtered.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = filtered, key = { it.id }) { recipe ->
                        RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe.id) })
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }

            if (isRefreshing) {
                Box(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Обновление...", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Загрузка рецептов...", fontSize = 14.sp)
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
            Text("Не удалось загрузить", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            Button(onClick = onRetry) { Text("Повторить") }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text("Ничего не найдено", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Попробуйте изменить категорию или запрос", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}