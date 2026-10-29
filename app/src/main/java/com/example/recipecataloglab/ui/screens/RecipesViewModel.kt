package com.example.recipecataloglab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipecataloglab.data.Recipe
import com.example.recipecataloglab.data.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ErrorType { NETWORK, TIMEOUT, SERVER }

sealed interface RecipesUiState {
    data object Loading : RecipesUiState
    data class Success(val recipes: List<Recipe>) : RecipesUiState
    data class Refreshing(val recipes: List<Recipe>) : RecipesUiState
    data class Error(val type: ErrorType, val message: String) : RecipesUiState
}

class RecipesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private var cachedRecipes: List<Recipe> = emptyList()
    private var activeJob: Job? = null
    private var retryCount = 0

    /**
     * Эмуляция сетевой загрузки.
     * Вызывается ОДИН РАЗ через LaunchedEffect(Unit) на экране списка.
     */
    fun loadRecipes(forceRefresh: Boolean = false) {
        if (activeJob?.isActive == true) return

        activeJob = viewModelScope.launch {
            // Логирование убрано для соответствия модели безопасности (T7)

            _uiState.value = when {
                forceRefresh || cachedRecipes.isEmpty() -> RecipesUiState.Loading
                else -> RecipesUiState.Refreshing(cachedRecipes)
            }

            try {
                delay(800)

                if (Random.nextInt(100) < 20) {
                    val errorType = ErrorType.values().random()
                    _uiState.value = RecipesUiState.Error(
                        type = errorType,
                        message = errorType.toMessage()
                    )
                    retryCount++
                    return@launch
                }

                val recipes = RecipeRepository.all.value
                cachedRecipes = recipes
                retryCount = 0
                _uiState.value = RecipesUiState.Success(recipes)

            } catch (e: Exception) {
                _uiState.value = RecipesUiState.Error(
                    type = ErrorType.SERVER,
                    message = e.message ?: "Неизвестная ошибка"
                )
                retryCount++
            }
        }
    }

    fun retry() {
        loadRecipes(forceRefresh = cachedRecipes.isEmpty())
    }

    fun refresh() {
        if (cachedRecipes.isEmpty()) {
            loadRecipes(forceRefresh = true)
        } else {
            loadRecipes(forceRefresh = false)
        }
    }

    /**
     * Эмуляция «серверного поиска» с дебаунсом.
     * Вызывается из LaunchedEffect(query) с задержкой 300 мс.
     */
    suspend fun searchRemote(query: String) {
        // Логирование убрано (T7)
        delay(250)
    }

    /**
     * Эмуляция «сохранения на сервер».
     * Пока идёт сохранение — isSaving = true (для анимации индикатора).
     */
    fun persistChanges() {
        viewModelScope.launch {
            _isSaving.value = true
            delay(400)
            _isSaving.value = false
        }
    }

    private fun ErrorType.toMessage(): String = when (this) {
        ErrorType.NETWORK -> "Нет подключения к интернету"
        ErrorType.TIMEOUT -> "Превышено время ожидания"
        ErrorType.SERVER -> "Ошибка сервера, попробуйте позже"
    }
}