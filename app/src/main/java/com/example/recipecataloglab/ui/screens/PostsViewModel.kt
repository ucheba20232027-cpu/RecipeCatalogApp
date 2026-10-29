package com.example.recipecataloglab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipecataloglab.data.Post
import com.example.recipecataloglab.data.PostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PostsUiState {
    data object Loading : PostsUiState
    data class Success(val posts: List<Post>) : PostsUiState
    data class Error(val message: String) : PostsUiState
}

class PostsViewModel : ViewModel() {
    private val repository = PostsRepository()
    private val _state = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val state: StateFlow<PostsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = PostsUiState.Loading
        viewModelScope.launch {
            repository.getPosts()
                .onSuccess { _state.value = PostsUiState.Success(it) }
                .onFailure { _state.value = PostsUiState.Error(it.message ?: "Неизвестная ошибка") }
        }
    }
}