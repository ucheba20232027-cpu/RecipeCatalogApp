package com.example.recipecataloglab.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipecataloglab.data.LocalNote
import com.example.recipecataloglab.data.NotesJson
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(
    onBack: () -> Unit,
    onEditNote: (Long) -> Unit,
    onAddNote: () -> Unit,
    viewModel: NotesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<LocalNote?>(null) }

    // 🔥 ВАЖНО: Перечитываем список каждый раз при возврате на экран
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    // Экспорт
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            val notes = (state as? NotesUiState.Success)?.notes ?: emptyList()
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(NotesJson.encode(notes).toByteArray())
                }
                Toast.makeText(context, "Экспортировано успешно", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка экспорта: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Импорт
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (jsonString != null) {
                    val imported = NotesJson.decode(jsonString)
                    viewModel.importNotes(imported)
                    Toast.makeText(context, "Импортировано: ${imported.size}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка импорта: неверный файл", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои локальные заметки") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Назад") }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Экспорт файла") },
                            onClick = { showMenu = false; exportLauncher.launch("my_local_notes.json") }
                        )
                        DropdownMenuItem(
                            text = { Text("Импорт файла") },
                            onClick = { showMenu = false; importLauncher.launch(arrayOf("application/json")) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is NotesUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is NotesUiState.Error -> {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(s.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.load() }) { Text("Повторить") }
                    }
                }
                is NotesUiState.Success -> {
                    if (s.notes.isEmpty()) {
                        Text("Заметок пока нет", Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(s.notes, key = { it.id }) { note ->
                                NoteItem(
                                    note = note,
                                    onClick = { onEditNote(note.id) },
                                    onLongClick = { noteToDelete = note }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог подтверждения удаления
    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Удалить заметку?") },
            text = { Text("Вы уверены, что хотите удалить '${noteToDelete?.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    noteToDelete?.let { viewModel.deleteNote(it.id) }
                    noteToDelete = null
                }) { Text("Удалить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) { Text("Отмена") }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteItem(note: LocalNote, onClick: () -> Unit, onLongClick: () -> Unit) {
    val formatter = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp)
    ) {
        Text(
            text = if (note.title.isBlank()) "Без названия" else note.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (note.body.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = note.body,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = formatter.format(Date(note.updatedAt)),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}