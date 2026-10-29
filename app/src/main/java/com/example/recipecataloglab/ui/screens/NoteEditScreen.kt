package com.example.recipecataloglab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipecataloglab.data.LocalNote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long,
    onBack: () -> Unit,
    viewModel: NotesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }

    // 🔥 Ждем, пока данные загрузятся, и заполняем поля
    LaunchedEffect(state, noteId) {
        if (!isLoaded && state is NotesUiState.Success) {
            if (noteId != 0L) {
                val existingNote = (state as NotesUiState.Success).notes.find { it.id == noteId }
                if (existingNote != null) {
                    title = existingNote.title
                    body = existingNote.body
                }
            }
            isLoaded = true
        }
    }

    // Функция сохранения
    fun saveAndExit() {
        if (title.isNotBlank() || body.isNotBlank()) {
            viewModel.saveNote(
                LocalNote(
                    id = if (noteId == 0L) System.currentTimeMillis() else noteId,
                    title = title.trim(),
                    body = body.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == 0L) "Новая заметка" else "Редактирование") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { saveAndExit() }) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        // Показываем индикатор загрузки, пока не прочитали заметку
        if (!isLoaded && noteId != 0L) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Текст заметки") },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { saveAndExit() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить заметку", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}