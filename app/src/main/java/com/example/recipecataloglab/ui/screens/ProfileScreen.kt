package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipecataloglab.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Автоскрытие сообщения через 2 секунды
    LaunchedEffect(showSavedMessage) {
        if (showSavedMessage) {
            delay(2000)
            showSavedMessage = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой профиль") },
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
                .padding(16.dp)
        ) {
            Text(
                "Мой профиль",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            ProfileCard(
                title = "Иван Кулинаров",
                subtitle = "Шеф-повар • 87 рецептов",
                leadingIcon = {
                    Text("👨‍🍳", fontSize = 36.sp)
                },
                trailingContent = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать профиль")
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Статистика",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Рецептов создано: 87",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Подписчиков: 1 234",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Средний рейтинг: 4.8",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(24.dp))

            AppButton(
                text = "Сохранить изменения",
                onClick = {
                    isLoading = true
                    scope.launch {
                        delay(1500)
                        isLoading = false
                        showSavedMessage = true
                    }
                },
                style = ButtonStyle.PRIMARY,
                size = ButtonSize.LARGE,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(Modifier.height(8.dp))

            AppButton(
                text = "Отмена",
                onClick = { },
                style = ButtonStyle.SECONDARY,
                size = ButtonSize.MEDIUM,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            AppButton(
                text = "Сбросить",
                onClick = { },
                style = ButtonStyle.OUTLINED,
                size = ButtonSize.SMALL
            )

            Spacer(Modifier.height(16.dp))

            LoadingButton(
                text = "Загрузить данные",
                onClick = {
                    isLoading = true
                    scope.launch {
                        delay(1500)
                        isLoading = false
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            if (showSavedMessage) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        "✓ Изменения сохранены",
                        modifier = Modifier
                            .padding(12.dp)
                            .semantics { liveRegion = LiveRegionMode.Assertive },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}