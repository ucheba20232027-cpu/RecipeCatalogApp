package com.example.recipecataloglab.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipecataloglab.data.LocalNote
import com.example.recipecataloglab.data.NotesFileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NotesUiState {
    data object Loading : NotesUiState
    data class Success(val notes: List<LocalNote>) : NotesUiState
    data class Error(val message: String) : NotesUiState
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotesFileRepository(application.filesDir)

    private val _state = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val state: StateFlow<NotesUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = NotesUiState.Loading
            repository.loadNotes()
                .onSuccess { notes ->
                    _state.value = NotesUiState.Success(notes.sortedByDescending { it.updatedAt })
                }
                .onFailure {
                    _state.value = NotesUiState.Error(it.message ?: "Ошибка чтения")
                }
        }
    }

    fun saveNote(note: LocalNote) {
        viewModelScope.launch {
            // 1. Получаем текущий список
            val currentNotes = (_state.value as? NotesUiState.Success)?.notes ?: emptyList()

            // 2. Обновляем список (заменяем старую версию, если есть, или добавляем новую)
            val updatedList = currentNotes.filterNot { it.id == note.id } + note

            // 3. Сортируем
            val sortedList = updatedList.sortedByDescending { it.updatedAt }

            // 4. СНАЧАЛА обновляем UI (мгновенный отклик)
            _state.value = NotesUiState.Success(sortedList)

            // 5. ЗАТЕМ сохраняем в файл в фоне
            repository.saveNotes(sortedList).onFailure {
                // Если файл не сохранился — показываем ошибку
                _state.value = NotesUiState.Error("Не удалось сохранить: ${it.message}")
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            val currentNotes = (_state.value as? NotesUiState.Success)?.notes ?: emptyList()
            val updatedList = currentNotes.filterNot { it.id == id }

            _state.value = NotesUiState.Success(updatedList)

            repository.saveNotes(updatedList).onFailure {
                _state.value = NotesUiState.Error("Не удалось удалить: ${it.message}")
            }
        }
    }

    fun importNotes(notes: List<LocalNote>) {
        viewModelScope.launch {
            val sortedList = notes.sortedByDescending { it.updatedAt }
            _state.value = NotesUiState.Success(sortedList)
            repository.saveNotes(sortedList)
        }
    }
}