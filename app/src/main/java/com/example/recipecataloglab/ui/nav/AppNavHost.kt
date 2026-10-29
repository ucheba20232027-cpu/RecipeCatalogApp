package com.example.recipecataloglab.ui.nav

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipecataloglab.ui.screens.*
import com.example.recipecataloglab.ui.theme.ThemeMode

object Routes {
    const val HOME = "home"
    const val LIST = "list"
    const val DETAIL = "details/{recipeId}"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val ADD_RECIPE = "add_recipe"

    // ЛР №10
    const val POSTS = "posts"
    const val POST_DETAIL = "post_details/{postId}"

    // ЛР №11
    const val NOTES = "notes"
    const val NOTE_EDIT = "note_edit/{noteId}"

    fun detail(id: Int) = "details/$id"
    fun postDetail(id: Int) = "post_details/$id"
    fun noteEdit(id: Long) = "note_edit/$id"
}

@Composable
fun AppNavHost(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    forceDarkContent: Boolean,
    onForceDarkChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = { slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = tween(300)) + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 4 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 4 }, animationSpec = tween(300)) + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onGoToList = { navController.navigate(Routes.LIST) },
                onGoToSettings = { navController.navigate(Routes.SETTINGS) },
                onGoToProfile = { navController.navigate(Routes.PROFILE) },
                onGoToPosts = { navController.navigate(Routes.POSTS) },
                onGoToNotes = { navController.navigate(Routes.NOTES) } // НОВОЕ
            )
        }

        composable(Routes.LIST) {
            ListScreen(
                onRecipeClick = { id -> navController.navigate(Routes.detail(id)) },
                onBack = { navController.popBackStack() },
                onAddRecipe = { navController.navigate(Routes.ADD_RECIPE) }
            )
        }

        composable(Routes.DETAIL, arguments = listOf(navArgument("recipeId") { type = NavType.IntType })) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: -1
            DetailScreen(recipeId = recipeId, onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                forceDarkContent = forceDarkContent,
                onForceDarkChange = onForceDarkChange,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ADD_RECIPE) {
            AddRecipeScreen(onBack = { navController.popBackStack() })
        }

        // Сетевые экраны (ЛР №10)
        composable(Routes.POSTS) {
            PostsScreen(
                onBack = { navController.popBackStack() },
                onPostClick = { id -> navController.navigate(Routes.postDetail(id)) }
            )
        }

        composable(Routes.POST_DETAIL, arguments = listOf(navArgument("postId") { type = NavType.IntType })) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: -1
            PostDetailScreen(postId = postId, onBack = { navController.popBackStack() })
        }

        // Экраны заметок (ЛР №11)
        composable(Routes.NOTES) {
            NotesScreen(
                onBack = { navController.popBackStack() },
                onEditNote = { id -> navController.navigate(Routes.noteEdit(id)) },
                onAddNote = { navController.navigate(Routes.noteEdit(0L)) }
            )
        }

        composable(Routes.NOTE_EDIT, arguments = listOf(navArgument("noteId") { type = NavType.LongType })) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            NoteEditScreen(noteId = noteId, onBack = { navController.popBackStack() })
        }
    }
}