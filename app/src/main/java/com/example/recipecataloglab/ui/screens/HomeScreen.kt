package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onGoToList: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToProfile: () -> Unit,
    onGoToPosts: () -> Unit,
    onGoToNotes: () -> Unit // НОВЫЙ ПАРАМЕТР
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🍲", fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text("Каталог рецептов", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Добро пожаловать! Здесь вы найдёте\nболее 20 проверенных рецептов\nна каждый день.",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        Button(onClick = onGoToList, modifier = Modifier.fillMaxWidth()) {
            Text("Перейти к рецептам")
        }
        Spacer(Modifier.height(8.dp))

        Button(onClick = onGoToNotes, modifier = Modifier.fillMaxWidth()) {
            Text("Мои заметки") // Кнопка для ЛР №11
        }
        Spacer(Modifier.height(8.dp))

        Button(onClick = onGoToPosts, modifier = Modifier.fillMaxWidth()) {
            Text("Посты из сети")
        }
        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onGoToSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Настройки")
        }
        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onGoToProfile, modifier = Modifier.fillMaxWidth()) {
            Text("Мой профиль")
        }
    }
}