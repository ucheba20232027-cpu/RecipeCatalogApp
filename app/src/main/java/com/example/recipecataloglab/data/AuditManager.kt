package com.example.recipecataloglab.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AuditManager {
    private const val FILE_NAME = "audit.log"

    suspend fun logEvent(context: Context, action: String, result: Boolean, details: String = "") = withContext(Dispatchers.IO) {
        // Безопасный вызов, если currentUser равен null
        val user = SessionManager.currentUser?.name ?: "Unknown"
        val role = SessionManager.currentUser?.role?.name ?: "GUEST"

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val status = if (result) "ALLOWED" else "DENIED"

        val logLine = "[$timestamp] User: $user | Role: $role | Action: $action | Result: $status | $details\n"

        try {
            val file = File(context.filesDir, FILE_NAME)
            file.appendText(logLine)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun readLogs(context: Context): String = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) file.readText() else "Журнал пуст."
    }
}