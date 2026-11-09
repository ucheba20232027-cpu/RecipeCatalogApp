package com.example.recipecataloglab.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recipecataloglab.data.AuditManager
import com.example.recipecataloglab.data.Recipe
import com.example.recipecataloglab.data.RecipeRepository
import com.example.recipecataloglab.ui.components.CameraHelper
import com.example.recipecataloglab.ui.components.CameraPermissionFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var emoji by rememberSaveable { mutableStateOf("🍽") }
    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Супы") }
    var timeText by rememberSaveable { mutableStateOf("") }
    var kcalText by rememberSaveable { mutableStateOf("") }
    var portionsText by rememberSaveable { mutableStateOf("") }
    var ingredients by rememberSaveable { mutableStateOf("") }
    var steps by rememberSaveable { mutableStateOf("") }

    // Состояние для фото
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentCameraUri by remember { mutableStateOf<Uri?>(null) }

    val isFormValid = title.isNotBlank() &&
            timeText.toIntOrNull() != null &&
            kcalText.toIntOrNull() != null &&
            portionsText.toIntOrNull() != null &&
            ingredients.isNotBlank() &&
            steps.isNotBlank()

    val categories = listOf("Супы", "Салаты", "Десерты", "Основное", "Выпечка", "Напитки", "Завтрак")

    // Лаунчер для камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri = currentCameraUri
            scope.launch {
                AuditManager.logEvent(context, "CAMERA_CAPTURE", true, "Фото сделано")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый рецепт") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Назад") }
                }
            )
        }
    ) { padding ->
        CameraPermissionFlow(
            onPermissionGranted = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text("Эмодзи блюда", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("🍽", "🍲", "🥗", "🥞", "🍕", "🍰", "🥤", "🥟", "🍜").forEach { e ->
                            FilterChip(
                                selected = emoji == e,
                                onClick = { emoji = e },
                                label = { Text(e, fontSize = 18.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // --- БЛОК ФОТО ---
                    if (capturedImageUri != null) {
                        // Показываем превью
                        Card(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            AsyncImage(
                                model = capturedImageUri,
                                contentDescription = "Фото блюда",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = { capturedImageUri = null },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Удалить фото", color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Button(
                            onClick = {
                                val uri = CameraHelper.createImageUri(context)
                                currentCameraUri = uri
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Сделать фото блюда")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ... (ОСТАЛЬНЫЕ ПОЛЯ ФОРМЫ: Название, Категория, Время, Ккал, Порции, Ингредиенты, Шаги)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Название блюда *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    // (Остальные поля такие же, как были, я их пропустил для краткости,
                    // но ты их оставляешь как в предыдущей версии)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = timeText, onValueChange = { if (it.all { ch -> ch.isDigit() }) timeText = it }, label = { Text("Мин *") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = kcalText, onValueChange = { if (it.all { ch -> ch.isDigit() }) kcalText = it }, label = { Text("Ккал *") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = portionsText, onValueChange = { if (it.all { ch -> ch.isDigit() }) portionsText = it }, label = { Text("Порц *") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Ингредиенты *") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = steps, onValueChange = { steps = it }, label = { Text("Приготовление *") }, minLines = 5, modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            RecipeRepository.add(
                                Recipe(
                                    id = 0,
                                    emoji = emoji,
                                    title = title.trim(),
                                    category = category,
                                    time = timeText.toIntOrNull() ?: 0,
                                    kcal = kcalText.toIntOrNull() ?: 0,
                                    portions = portionsText.toIntOrNull() ?: 1,
                                    ingredients = ingredients.trim(),
                                    steps = steps.trim(),
                                    imagePath = capturedImageUri?.toString() // Сохраняем путь к фото!
                                )
                            )
                            scope.launch { AuditManager.logEvent(context, "ADD_RECIPE", true, "Рецепт '${title.trim()}'") }
                            onBack()
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Сохранить рецепт")
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        )
    }
}