package com.example.recipecataloglab.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class NotesFileRepository(
    private val filesDir: File,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val file = File(filesDir, "notes.json")

    suspend fun loadNotes(): Result<List<LocalNote>> = withContext(ioDispatcher) {
        runCatching {
            if (file.exists()) NotesJson.decode(file.readText()) else emptyList()
        }
    }

    suspend fun saveNotes(notes: List<LocalNote>): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            // Атомарная запись
            val tmp = File(filesDir, "notes.json.tmp")
            tmp.writeText(NotesJson.encode(notes))
            if (file.exists()) file.delete()
            tmp.renameTo(file)
            Unit // <-- Вот это исправляет ошибку Result<Boolean> -> Result<Unit>
        }
    }
}