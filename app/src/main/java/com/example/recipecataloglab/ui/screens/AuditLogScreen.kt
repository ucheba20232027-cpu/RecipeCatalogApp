package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipecataloglab.data.AuditManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditLogScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var logText by remember { mutableStateOf("Загрузка...") }

    fun refreshLogs() {
        scope.launch {
            logText = AuditManager.readLogs(context)
        }
    }

    // Загружаем логи при открытии
    LaunchedEffect(Unit) {
        refreshLogs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Журнал аудита") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Назад") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = logText,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }

            // Кнопка очистки внизу экрана
            Button(
                onClick = {
                    scope.launch {
                        val file = File(context.filesDir, "audit.log")
                        if (file.exists()) {
                            file.writeText("") // Очищаем содержимое, не удаляя файл
                        }
                        // Добавляем запись о том, что журнал очищен (это тоже событие аудита)
                        AuditManager.logEvent(context, "CLEAR_AUDIT", true, "Журнал очищен вручную")
                        delay(100) // Небольшая задержка, чтобы запись успела сохраниться
                        refreshLogs()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Очистить журнал")
            }
        }
    }
}