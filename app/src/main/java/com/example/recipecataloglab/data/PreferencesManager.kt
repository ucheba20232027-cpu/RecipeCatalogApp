package com.example.recipecataloglab.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var savedPin: String?
        get() = prefs.getString("user_pin", null)
        set(value) = prefs.edit().putString("user_pin", value).apply()

    var savedUserLogin: String?
        get() = prefs.getString("user_login", null)
        set(value) = prefs.edit().putString("user_login", value).apply()

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)
        set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

    // НОВОЕ: Сохранение профиля
    var customUserName: String?
        get() = prefs.getString("custom_user_name", null)
        set(value) = prefs.edit().putString("custom_user_name", value).apply()

    var customUserAvatar: String?
        get() = prefs.getString("custom_user_avatar", null)
        set(value) = prefs.edit().putString("custom_user_avatar", value).apply()

    fun clearAllData(context: Context) {
        prefs.edit().clear().apply()
        val notesFile = java.io.File(context.filesDir, "notes.json")
        if (notesFile.exists()) {
            notesFile.delete()
        }
    }
}