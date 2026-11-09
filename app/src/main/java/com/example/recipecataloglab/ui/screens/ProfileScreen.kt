package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipecataloglab.data.PreferencesManager
import com.example.recipecataloglab.data.SessionManager
import com.example.recipecataloglab.data.UserDatabase
import com.example.recipecataloglab.data.UserRole
import com.example.recipecataloglab.ui.components.AppButton
import com.example.recipecataloglab.ui.components.ButtonSize
import com.example.recipecataloglab.ui.components.ButtonStyle
import com.example.recipecataloglab.ui.components.ProfileCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    var currentUser by remember { mutableStateOf(SessionManager.currentUser) }

    val userName = currentUser?.name ?: "Гость"
    val userRole = if (currentUser?.role == UserRole.ADMIN) "Администратор" else "Пользователь"
    val userAvatar = currentUser?.avatar ?: "👤"

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            ProfileCard(
                title = userName,
                subtitle = userRole,
                leadingIcon = {
                    Text(userAvatar, fontSize = 48.sp)
                },
                trailingContent = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать профиль")
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("87", "Рецептов")
                StatItem("1.2K", "Подписчиков")
                StatItem("4.8", "Рейтинг")
            }

            Spacer(Modifier.height(48.dp))

            AppButton(
                text = "Выйти из аккаунта",
                onClick = { showExitDialog = true },
                style = ButtonStyle.OUTLINED,
                size = ButtonSize.LARGE,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showEditDialog && currentUser != null) {
        var newName by remember { mutableStateOf(currentUser!!.name) }
        var newAvatar by remember { mutableStateOf(currentUser!!.avatar) }
        val avatars = listOf("👨‍🍳", "👩‍💼", "🧑‍🍳", "👨‍🎓", "👩‍🎓", "🦸‍♂️", "🦸‍♀️", "🐱")

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Редактировать профиль") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Имя") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Выберите аватар", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        avatars.take(4).forEach { emoji ->
                            AvatarChip(emoji, newAvatar == emoji) { newAvatar = emoji }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        avatars.drop(4).forEach { emoji ->
                            AvatarChip(emoji, newAvatar == emoji) { newAvatar = emoji }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // 1. Обновляем объект пользователя в памяти
                    val updated = currentUser!!.copy(name = newName, avatar = newAvatar)
                    SessionManager.currentUser = updated
                    currentUser = updated

                    // 2. СОХРАНЯЕМ В ПАМЯТЬ УСТРОЙСТВА (Preferences)
                    prefs.customUserName = newName
                    prefs.customUserAvatar = newAvatar

                    showEditDialog = false
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Выход из аккаунта") },
            text = { Text("Вы уверены, что хотите выйти? Для повторного входа потребуется PIN-код или логин.") },
            confirmButton = {
                TextButton(onClick = {
                    // 1. Сбрасываем пользователя
                    SessionManager.currentUser = null

                    // 2. Удаляем PIN и логин из памяти, чтобы выкинуть на экран авторизации
                    prefs.savedPin = null
                    prefs.savedUserLogin = null
                    prefs.isLoggedIn = false

                    showExitDialog = false
                    onLogout() // Сообщаем MainActivity
                }) {
                    Text("Выйти", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AvatarChip(emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 24.sp)
        }
    }
}