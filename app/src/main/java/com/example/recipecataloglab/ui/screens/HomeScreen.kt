package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipecataloglab.data.SessionManager
import com.example.recipecataloglab.data.UserRole

@Composable
fun HomeScreen(
    onGoToList: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToProfile: () -> Unit,
    onGoToPosts: () -> Unit,
    onGoToNotes: () -> Unit,
    onGoToAudit: () -> Unit
) {
    val isAdmin = SessionManager.currentUser?.role == UserRole.ADMIN
    val userName = SessionManager.currentUser?.name ?: "Гость"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- ШАПКА ---
        Text(
            "Привет, $userName!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        Text(
            "Ваш личный кулинарный помощник",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- ГЛАВНАЯ КАРТОЧКА (КАТАЛОГ) ---
        Card(
            onClick = onGoToList,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(
                        "Каталог рецептов",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Более 20 блюд на каждый день",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    Icons.Default.RestaurantMenu,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- БЛОК: ЛИЧНОЕ ---
        Text(
            "Личное",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        HomeButton(
            title = "Мои рецепты",
            subtitle = "Ваши сохраненные записи (Защищено PIN)",
            icon = Icons.Default.Book,
            onClick = onGoToNotes
        )

        HomeButton(
            title = "Мой профиль",
            subtitle = "Статистика и настройки аккаунта",
            icon = Icons.Default.Person,
            onClick = onGoToProfile
        )

        Spacer(Modifier.height(16.dp))

        // --- БЛОК: СЕТЬ И РАЗВЛЕЧЕНИЯ ---
        Text(
            "Дополнительно",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        HomeButton(
            title = "Посты из сети",
            subtitle = "Загрузить данные с сервера",
            icon = Icons.Default.Cloud,
            onClick = onGoToPosts
        )

        HomeButton(
            title = "Настройки",
            subtitle = "Темная тема и оформление",
            icon = Icons.Default.Settings,
            onClick = onGoToSettings
        )

        // --- БЛОК: АДМИНИСТРИРОВАНИЕ (Только для админа) ---
        if (isAdmin) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Администрирование",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            HomeButton(
                title = "Журнал аудита",
                subtitle = "Просмотр всех действий пользователей",
                icon = Icons.Default.Security,
                onClick = onGoToAudit
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun HomeButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}