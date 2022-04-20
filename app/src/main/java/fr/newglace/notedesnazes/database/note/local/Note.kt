package fr.newglace.notedesnazes.database.note.local

import java.io.Serializable

class Note(
        val noteTitle: String,
        val noteContent: String,
        val isFavorite: Boolean,
        val password: String,
        val visual: String,
        val folder: String,
        val position: Int,
        val folderPosition: Int,
        val colorFolder: String
) : Serializable
