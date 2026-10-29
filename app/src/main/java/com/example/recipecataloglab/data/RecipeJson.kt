package com.example.recipecataloglab.data

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

object RecipeJson {
    private val format = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    // Явно указываем сериализатор для списка Recipe
    private val listSerializer = ListSerializer(Recipe.serializer())

    fun encode(recipes: List<Recipe>): String =
        format.encodeToString(listSerializer, recipes)

    fun decode(jsonString: String): List<Recipe> =
        format.decodeFromString(listSerializer, jsonString)
}