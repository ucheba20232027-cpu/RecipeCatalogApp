package com.example.recipecataloglab.data

import kotlinx.serialization.Serializable

@Serializable
data class LocalNote(
    val id: Long = System.currentTimeMillis(),
    val title: String = "",
    val body: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)