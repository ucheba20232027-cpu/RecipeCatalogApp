package com.example.recipecataloglab

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.fragment.app.FragmentActivity
import com.example.recipecataloglab.data.RecipeRepository
import com.example.recipecataloglab.ui.nav.AppNavHost
import com.example.recipecataloglab.ui.screens.PinLockScreen
import com.example.recipecataloglab.ui.theme.AppTheme
import com.example.recipecataloglab.ui.theme.ThemeMode

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация репозитория (загрузка рецептов из файла)
        RecipeRepository.init(applicationContext)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContent {
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            var forceDarkContent by rememberSaveable { mutableStateOf(false) }
            var isUnlocked by remember { mutableStateOf(false) }

            AppTheme(themeMode = themeMode, forceDarkContent = forceDarkContent) {
                if (!isUnlocked) {
                    PinLockScreen(onUnlocked = { isUnlocked = true })
                } else {
                    AppNavHost(
                        themeMode = themeMode,
                        onThemeModeChange = { themeMode = it },
                        forceDarkContent = forceDarkContent,
                        onForceDarkChange = { forceDarkContent = it },
                        onLogout = { isUnlocked = false }
                    )
                }
            }
        }
    }
}