package com.example.notetakingtwo.Interfaces

import android.content.Context
import com.example.notetakingtwo.Models.Note
import com.example.notetakingtwo.Models.User
import retrofit2.Response

interface NoteApiDataSource {
   suspend fun addNote(noteData: Note): Response<Note>
    suspend fun deleteNote(note: Note): Response<Note>
    suspend fun getNotes(id: String): Response<List<Note>>
    suspend fun updateNote(note: Note): Response<Note>
}