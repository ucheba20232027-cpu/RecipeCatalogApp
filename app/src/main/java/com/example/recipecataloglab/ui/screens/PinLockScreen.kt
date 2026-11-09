package com.example.recipecataloglab.ui.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.recipecataloglab.data.PreferencesManager
import com.example.recipecataloglab.data.SessionManager
import com.example.recipecataloglab.data.UserDatabase
import kotlin.random.Random

@Composable
fun PinLockScreen(onUnlocked: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }

    // Определяем шаг: если PIN уже есть, сразу просим его. Иначе — логин.
    var currentStep by remember {
        mutableStateOf(if (prefs.savedPin == null) "LOGIN" else "PIN_ENTRY")
    }

    var generatedOtp by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    // --- ФУНКЦИЯ ДЛЯ ВХОДА ПО PIN ---
    fun verifyPin(inputPin: String) {
        if (inputPin == prefs.savedPin) {
            // PIN верный. Находим пользователя по сохраненному логину, передавая prefs
            val login = prefs.savedUserLogin
            val user = login?.let { UserDatabase.findByLogin(it, prefs) }
            if (user != null) {
                SessionManager.currentUser = user
                onUnlocked()
            } else {
                error = "Ошибка профиля. Сбросьте PIN."
            }
        } else {
            error = "Неверный PIN-код"
            pin = ""
        }
    }

    // --- ЭКРАН 1: ЛОГИН И ПАРОЛЬ (Первый запуск) ---
    if (currentStep == "LOGIN") {
        var login by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var loginError by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("👤", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("Вход в систему", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Логин: admin / user\nПароль: 1234", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Логин") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (loginError != null) {
                Spacer(Modifier.height(8.dp))
                Text(loginError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    // Находим пользователя, передавая prefs
                    val user = UserDatabase.findByLogin(login, prefs)
                    if (user != null && password == "1234") {
                        prefs.savedUserLogin = login
                        generatedOtp = Random.nextInt(1000, 9999).toString()
                        currentStep = "OTP"
                        loginError = null
                        android.widget.Toast.makeText(context, "СМС код: $generatedOtp", android.widget.Toast.LENGTH_LONG).show()
                    } else {
                        loginError = "Неверный логин или пароль"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Войти")
            }
        }
    }

    // --- ЭКРАН 2: СМС КОД (Первый запуск) ---
    if (currentStep == "OTP") {
        var otpInput by remember { mutableStateOf("") }
        var otpError by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📱", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("Введите код из СМС", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = otpInput,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        otpInput = it
                        otpError = null
                        if (it.length == 4) {
                            if (it == generatedOtp) {
                                currentStep = "PIN_SETUP"
                            } else {
                                otpError = "Неверный код"
                                otpInput = ""
                            }
                        }
                    }
                },
                label = { Text("Код из СМС") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (otpError != null) {
                Spacer(Modifier.height(8.dp))
                Text(otpError!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    // --- ЭКРАН 3: УСТАНОВКА PIN (Первый запуск) ---
    if (currentStep == "PIN_SETUP") {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🛡️", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("Придумайте PIN-код", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        pin = it
                        if (it.length == 4) {
                            prefs.savedPin = it
                            val login = prefs.savedUserLogin
                            val user = login?.let { UserDatabase.findByLogin(it, prefs) }
                            if (user != null) {
                                SessionManager.currentUser = user
                                onUnlocked()
                            }
                        }
                    }
                },
                label = { Text("PIN (4 цифры)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }

    // --- ЭКРАН 4: ВВОД PIN (Повторные запуски) ---
    if (currentStep == "PIN_ENTRY") {
        val biometricManager = BiometricManager.from(context)
        val canUseBiometric = remember {
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
            ) == BiometricManager.BIOMETRIC_SUCCESS
        }

        fun showBiometricPrompt() {
            val activity = context as? FragmentActivity ?: return
            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        val login = prefs.savedUserLogin
                        val user = login?.let { UserDatabase.findByLogin(it, prefs) } // Передаем prefs
                        if (user != null) {
                            SessionManager.currentUser = user
                            onUnlocked()
                        }
                    }
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {}
                    override fun onAuthenticationFailed() { error = "Отпечаток не распознан" }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Вход в приложение")
                .setSubtitle("Приложите палец к датчику")
                .setNegativeButtonText("Использовать PIN")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
                .build()

            biometricPrompt.authenticate(promptInfo)
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Сброс PIN-кода") },
                text = { Text("Внимание! Все ваши личные данные будут безвозвратно удалены. Продолжить?") },
                confirmButton = {
                    TextButton(onClick = {
                        prefs.clearAllData(context)
                        currentStep = "LOGIN"
                        pin = ""
                        error = null
                        showResetDialog = false
                    }) {
                        Text("Удалить и сбросить", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) { Text("Отмена") }
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔒", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("Введите PIN-код", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        pin = it
                        error = null
                        if (it.length == 4) {
                            verifyPin(it)
                        }
                    }
                },
                label = { Text("PIN (4 цифры)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            if (canUseBiometric) {
                OutlinedButton(
                    onClick = { showBiometricPrompt() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Войти по отпечатку")
                }
                Spacer(Modifier.height(16.dp))
            }

            TextButton(onClick = { showResetDialog = true }) {
                Text("Забыли PIN?", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}