package com.example.notetakingtwo.repositories

import android.content.Context
import com.example.notetakingtwo.Interfaces.NoteApiDataSource
import com.example.notetakingtwo.Models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response





class NotesRepository(private val networkDataSource: NoteApiDataSource) {
    suspend fun addNote(data: Note): Note {
        try {
            return withContext(Dispatchers.IO) {
                return@withContext networkDataSource.addNote(data).body()!!
            }
        }
        catch (e: Exception) {
            throw Exception("It seems we have a problem with connecting to the server (${e})")
        }
    }

    suspend fun deleteNote(data: Note): Note? {
        try {
            return withContext(Dispatchers.IO) {
                return@withContext networkDataSource.deleteNote(data).body()
            }
        }
        catch (e: Exception) {
            throw Exception("It seems we have a problem with connecting to the server (${e})")
        }
    }
    suspend fun getNotes(id: String): List<Note> {
        try {
            return withContext(Dispatchers.IO) {
                return@withContext networkDataSource.getNotes(id).body()!!
            }
        }
        catch (e: Exception) {
            throw Exception("It seems we have a problem with connecting to the server (${e})")
        }
    }
    suspend fun updateNote(note: Note): Note {
        try {
            return withContext(Dispatchers.IO) {
                return@withContext networkDataSource.updateNote(note).body()!!
            }
        }
        catch (e: Exception) {
            throw Exception("It seems we have a problem with connecting to the server (${e})")
        }
    }

}