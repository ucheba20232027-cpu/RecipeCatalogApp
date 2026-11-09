package com.example.recipecataloglab.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun CameraPermissionFlow(
    onPermissionGranted: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Проверяем разрешение при каждом пересоздании
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isDenied by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            isDenied = true
            showRationale = false
        }
    }

    if (hasPermission) {
        onPermissionGranted()
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📸", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "Доступ к камере",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Разрешите доступ к камере, чтобы сфотографировать готовое блюдо и сохранить его как обложку рецепта.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            if (isDenied) {
                // Если пользователь отказал (или нажал "Не спрашивать снова")
                Button(
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Открыть настройки")
                }
                TextButton(onClick = {
                    isDenied = false
                    showRationale = true
                }) {
                    Text("Попробовать снова")
                }
            } else if (showRationale) {
                // Объясняем, зачем нужно разрешение
                Text(
                    "Без камеры вы не сможете сделать фото. Нажмите 'Разрешить', чтобы продолжить.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { launcher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Разрешить камеру")
                }
            } else {
                Button(
                    onClick = {
                        // Показываем rationale перед запросом
                        showRationale = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Разрешить камеру")
                }
            }
        }
    }
}