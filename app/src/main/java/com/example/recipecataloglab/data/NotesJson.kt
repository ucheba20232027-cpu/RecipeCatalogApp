package com.example.recipecataloglab.data

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

object NotesJson {
    private val format = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val listSerializer = ListSerializer(LocalNote.serializer())

    fun encode(notes: List<LocalNote>): String =
        format.encodeToString(listSerializer, notes)

    fun decode(text: String): List<LocalNote> =
        format.decodeFromString(listSerializer, text)
}