package com.example.recipecataloglab.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Recipe(
    val id: Int,
    val emoji: String,
    val title: String,
    val category: String,
    val time: Int,
    val kcal: Int,
    val portions: Int,
    val ingredients: String,
    val steps: String,
    val imagePath: String? = null // <-- НОВОЕ ПОЛЕ
)