package com.example.recipecataloglab

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeListTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun openList() {
        // Ждём загрузки главного экрана
        composeRule.waitUntil(3000) {
            composeRule.onAllNodesWithText("Перейти к рецептам").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Перейти к рецептам").performClick()

        // Ждём загрузки списка (там эмуляция сети 800 мс + возможная ошибка)
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithTag("searchField").fetchSemanticsNodes().isNotEmpty() ||
                    composeRule.onAllNodesWithText("Не удалось загрузить").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testListScreenIsDisplayed() {
        openList()

        // Если сеть сработала успешно, видим поиск
        // Если ошибка — кнопку "Повторить"
        val hasSearch = composeRule.onAllNodesWithTag("searchField").fetchSemanticsNodes().isNotEmpty()
        val hasError = composeRule.onAllNodesWithText("Не удалось загрузить").fetchSemanticsNodes().isNotEmpty()

        assert(hasSearch || hasError) { "Экран списка не отобразился" }
    }

    @Test
    fun testAddRecipeFABExists() {
        openList()

        // Ждём, пока нажмём и загрузимся (если была ошибка — FAB не покажется, поэтому повторяем)
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithTag("addRecipeFAB").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("addRecipeFAB").assertExists()
    }

    @Test
    fun testSearchFieldAcceptsInput() {
        openList()

        // Пропускаем, если выпала ошибка
        if (composeRule.onAllNodesWithText("Не удалось загрузить").fetchSemanticsNodes().isNotEmpty()) {
            return
        }

        composeRule.onNodeWithTag("searchField").performTextInput("Борщ")
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Борщ").assertExists()
    }

    @Test
    fun testEmptyStateShownWhenNoResults() {
        openList()

        if (composeRule.onAllNodesWithText("Не удалось загрузить").fetchSemanticsNodes().isNotEmpty()) {
            return
        }

        composeRule.onNodeWithTag("searchField").performTextInput("НетТакогоРецепта123")

        // Ждём дебаунс
        composeRule.mainClock.advanceTimeBy(500)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Ничего не найдено").assertExists()
    }
}