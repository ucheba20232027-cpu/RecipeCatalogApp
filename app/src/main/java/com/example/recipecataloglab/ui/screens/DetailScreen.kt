package com.example.recipecataloglab.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recipecataloglab.data.RecipeRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipeId: Int,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val recipe = if (recipeId > 0) RecipeRepository.findById(recipeId) else null

    if (recipe == null) {
        NotFoundScreen(recipeId = recipeId, onBack = onBack)
        return
    }

    var portions by rememberSaveable(recipeId) { mutableStateOf(recipe.portions) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title, fontSize = 18.sp) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ФОТО ИЛИ ЭМОДЗИ ---
            if (recipe.imagePath != null) {
                AsyncImage(
                    model = recipe.imagePath,
                    contentDescription = "Фото блюда",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                Text(recipe.emoji, fontSize = 80.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                recipe.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${recipe.category} • ${recipe.time} мин",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            // --- СТАТИСТИКА ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoCell("${recipe.kcal}", "Ккал")
                Spacer(Modifier.weight(1f))
                InfoCell("$portions", "Порции")
                Spacer(Modifier.weight(1f))
                InfoCell("${recipe.time}", "Минут")
            }

            Spacer(Modifier.height(20.dp))

            // --- СЧЕТЧИК ПОРЦИЙ ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Количество порций",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (portions > 1) portions-- }
                        ) {
                            Text("−1")
                        }
                        Text(
                            "$portions",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = { portions++ }
                        ) {
                            Text("+1")
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // --- ИНГРЕДИЕНТЫ ---
            SectionTitle("Ингредиенты")
            Text(
                recipe.ingredients,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(20.dp))

            // --- ШАГИ ---
            SectionTitle("Приготовление")
            Text(
                recipe.steps,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(24.dp))

            // --- КНОПКИ ---
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Приготовить")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("В избранное")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoCell(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun NotFoundScreen(recipeId: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("😕", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Рецепт не найден",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "ID рецепта: $recipeId. Возможно, он был удалён\nили ссылка неверна.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Вернуться к списку")
        }
    }
}