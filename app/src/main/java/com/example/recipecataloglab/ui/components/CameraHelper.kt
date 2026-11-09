package com.example.recipecataloglab.ui.components

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object CameraHelper {
    fun createImageUri(context: Context): Uri {
        // Используем filesDir вместо cacheDir для постоянного хранения
        val imageDir = File(context.filesDir, "recipe_images")
        if (!imageDir.exists()) imageDir.mkdirs()

        val imageFile = File(imageDir, "recipe_${System.currentTimeMillis()}.jpg")

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }
}