package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GuideItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuideScreen(onBack: () -> Unit) {
    val guideItems = listOf(
        GuideItem(
            "Добавление рецепта",
            "Нажмите на кнопку «+» в каталоге рецептов. Заполните название, ингредиенты и шаги приготовления.",
            Icons.Default.AddCircle
        ),
        GuideItem(
            "Сохранение рецептов",
            "Все ваши рецепты автоматически сохраняются во внутренней памяти устройства. Они доступны даже без интернета.",
            Icons.Default.Save
        ),
        GuideItem(
            "Мои рецепты (Заметки)",
            "В этом разделе вы можете вести личные заметки. Доступ к ним защищён PIN-кодом и отпечатком пальца.",
            Icons.Default.Book
        ),
        GuideItem(
            "Поиск по категориям",
            "Используйте чипсы вверху каталога, чтобы быстро отфильтровать рецепты по категориям (Супы, Салаты и т.д.).",
            Icons.Default.Search
        ),
        GuideItem(
            "Смена темы",
            "В Настройках можно переключить приложение на светлую или тёмную тему, либо выбрать системную.",
            Icons.Default.Settings
        ),
        GuideItem(
            "Журнал аудита",
            "Доступен только администратору. Здесь записываются все действия пользователей для безопасности.",
            Icons.Default.Security
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Руководство пользователя") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Назад") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Добро пожаловать в Каталог Рецептов!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Здесь собраны полезные советы, которые помогут вам быстро освоиться в приложении.",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(guideItems) { item ->
                GuideCard(item)
            }
        }
    }
}

@Composable
private fun GuideCard(item: GuideItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}