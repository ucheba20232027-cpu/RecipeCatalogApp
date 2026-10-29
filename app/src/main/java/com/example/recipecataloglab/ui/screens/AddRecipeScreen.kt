package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipecataloglab.data.Recipe
import com.example.recipecataloglab.data.RecipeRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(onBack: () -> Unit) {
    var emoji by rememberSaveable { mutableStateOf("🍽") }
    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Супы") }
    var timeText by rememberSaveable { mutableStateOf("") }
    var kcalText by rememberSaveable { mutableStateOf("") }
    var portionsText by rememberSaveable { mutableStateOf("") }
    var ingredients by rememberSaveable { mutableStateOf("") }
    var steps by rememberSaveable { mutableStateOf("") }

    val isFormValid = title.isNotBlank() &&
            timeText.toIntOrNull() != null &&
            kcalText.toIntOrNull() != null &&
            portionsText.toIntOrNull() != null &&
            ingredients.isNotBlank() &&
            steps.isNotBlank()

    val categories = listOf("Супы", "Салаты", "Десерты", "Основное", "Выпечка", "Напитки", "Завтрак")

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

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название блюда *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Text("Категория", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(4).forEach { c ->
                    FilterChip(
                        selected = category == c,
                        onClick = { category = c },
                        label = { Text(c, fontSize = 12.sp) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.drop(4).forEach { c ->
                    FilterChip(
                        selected = category == c,
                        onClick = { category = c },
                        label = { Text(c, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) timeText = it },
                    label = { Text("Минут *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = kcalText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) kcalText = it },
                    label = { Text("Ккал *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = portionsText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) portionsText = it },
                    label = { Text("Порций *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Ингредиенты *") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = steps,
                onValueChange = { steps = it },
                label = { Text("Приготовление *") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

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
                            steps = steps.trim()
                        )
                    )
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
}