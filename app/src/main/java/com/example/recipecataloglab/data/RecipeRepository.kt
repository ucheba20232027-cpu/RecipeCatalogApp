package com.example.recipecataloglab.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object RecipeRepository {
    private lateinit var appContext: Context
    private val _all = MutableStateFlow<List<Recipe>>(emptyList())
    val all: StateFlow<List<Recipe>> = _all.asStateFlow()

    // Инициализация (вызывать в MainActivity при старте)
    fun init(context: Context) {
        appContext = context.applicationContext
        loadRecipes()
    }

    private fun loadRecipes() {
        try {
            val file = File(appContext.filesDir, "recipes.json")
            if (file.exists()) {
                val json = file.readText()
                _all.value = RecipeJson.decode(json)
            } else {
                // Если файла нет, загружаем дефолтные рецепты (как было раньше)
                _all.value = getDefaultRecipes()
                saveRecipes()
            }
        } catch (e: Exception) {
            _all.value = getDefaultRecipes()
        }
    }

    private fun saveRecipes() {
        try {
            val file = File(appContext.filesDir, "recipes.json")
            file.writeText(RecipeJson.encode(_all.value))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun findById(id: Int): Recipe? = _all.value.find { it.id == id }

    fun add(recipe: Recipe) {
        val nextId = (_all.value.maxOfOrNull { it.id } ?: 0) + 1
        _all.value = _all.value + recipe.copy(id = nextId)
        saveRecipes()
    }

    fun update(recipe: Recipe) {
        _all.value = _all.value.map { if (it.id == recipe.id) recipe else it }
        saveRecipes()
    }

    fun remove(id: Int) {
        _all.value = _all.value.filterNot { it.id == id }
        saveRecipes()
    }

    fun replaceAll(newRecipes: List<Recipe>) {
        _all.value = newRecipes
        saveRecipes()
    }

    val categories: List<String>
        get() = listOf("Все") + _all.value.map { it.category }.distinct().sorted()

    private fun getDefaultRecipes(): List<Recipe> {
        return listOf(
            Recipe(1, "🍲", "Борщ классический", "Супы", 90, 320, 4, "Свёкла, капуста, мясо, картофель, лук, морковь", "1. Сварить бульон.\n2. Добавить картофель.\n3. Обжарить овощи.\n4. Соединить."),
            Recipe(2, "🥗", "Оливье", "Салаты", 40, 250, 6, "Картофель, морковь, яйца, горошек, колбаса, майонез", "1. Отварить.\n2. Нарезать.\n3. Заправить."),
            Recipe(3, "🥞", "Панкейки", "Десерты", 20, 180, 3, "Мука, молоко, яйцо, сахар", "1. Смешать.\n2. Обжарить."),
            Recipe(4, "🥟", "Пельмени", "Основное", 60, 420, 5, "Мука, яйцо, вода, фарш", "1. Замесить тесто.\n2. Слепить.\n3. Отварить."),
            Recipe(5, "🧀", "Сырники", "Завтрак", 25, 290, 4, "Творог, яйцо, мука", "1. Смешать.\n2. Обжарить."),
            Recipe(6, "🍕", "Пицца", "Выпечка", 50, 550, 4, "Тесто, соус, сыр", "1. Раскатать.\n2. Выпекать."),
            Recipe(7, "🍰", "Тирамису", "Десерты", 45, 380, 6, "Маскарпоне, печенье, кофе", "1. Слои.\n2. Охладить."),
            Recipe(8, "🥤", "Лимонад", "Напитки", 10, 90, 4, "Лимоны, вода, сахар", "1. Смешать."),
            Recipe(9, "🍜", "Лапша вок", "Основное", 30, 480, 2, "Лапша, курица, овощи", "1. Обжарить."),
            Recipe(10, "🍣", "Роллы", "Основное", 60, 350, 4, "Рис, нори, лосось", "1. Свернуть.\n2. Нарезать."),
            Recipe(11, "🍲", "Щи", "Супы", 75, 280, 5, "Капуста, мясо, картофель", "1. Сварить."),
            Recipe(12, "🥗", "Греческий салат", "Салаты", 15, 200, 3, "Огурцы, помидоры, фета", "1. Нарезать."),
            Recipe(13, "🥞", "Блины", "Выпечка", 40, 310, 6, "Мука, молоко, яйца", "1. Испечь."),
            Recipe(14, "🍕", "Пепперони", "Выпечка", 50, 620, 4, "Тесто, сыр, колбаса", "1. Выпекать."),
            Recipe(15, "🍰", "Чизкейк", "Десерты", 90, 450, 8, "Сыр, печенье, масло", "1. Выпекать."),
            Recipe(16, "🥤", "Смузи", "Напитки", 5, 120, 2, "Клубника, банан, йогурт", "1. Взбить."),
            Recipe(17, "🍜", "Том Ям", "Супы", 45, 300, 4, "Креветки, молоко, грибы", "1. Сварить."),
            Recipe(18, "🥗", "Цезарь", "Салаты", 30, 380, 3, "Курица, салат, соус", "1. Смешать."),
            Recipe(19, "🧀", "Омлет", "Завтрак", 10, 250, 2, "Яйца, молоко, сыр", "1. Обжарить."),
            Recipe(20, "🥟", "Вареники", "Десерты", 50, 320, 5, "Мука, вода, вишня", "1. Отварить.")
        )
    }
}