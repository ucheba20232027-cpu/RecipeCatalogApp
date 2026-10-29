package com.example.recipecataloglab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.recipecataloglab.ui.nav.AppNavHost
import com.example.recipecataloglab.ui.theme.AppTheme
import com.example.recipecataloglab.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            var forceDarkContent by rememberSaveable { mutableStateOf(false) }

            AppTheme(
                themeMode = themeMode,
                forceDarkContent = forceDarkContent
            ) {
                AppNavHost(
                    themeMode = themeMode,
                    onThemeModeChange = { themeMode = it },
                    forceDarkContent = forceDarkContent,
                    onForceDarkChange = { forceDarkContent = it }
                )
            }
        }
    }
}