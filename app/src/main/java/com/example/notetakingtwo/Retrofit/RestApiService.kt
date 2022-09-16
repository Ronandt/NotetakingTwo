package com.example.notetakingtwo.Retrofit

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.notetakingtwo.Interfaces.NoteApiDataSource
import com.example.notetakingtwo.Interfaces.UserApiDataSource
import com.example.notetakingtwo.Models.LoginResponse
import com.example.notetakingtwo.Models.Note
import com.example.notetakingtwo.Models.User
import com.example.notetakingtwo.NotesApi
import com.example.notetakingtwo.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NoteRestApiService: NoteApiDataSource, UserApiDataSource {
    private val retrofitInstance = RetrofitHelper.getInstance().create(NotesApi::class.java)

   override suspend fun addNote(noteData: Note): Response<Note> {

            return retrofitInstance.createNote(noteData)
    }


   override suspend fun registerUser(userData: User): Response<User> {

     return retrofitInstance.registerUser(userData)
    }

    override suspend fun getNotes(id: String): Response<List<Note>> {
        return retrofitInstance.getNotes(id)
    }

    override suspend fun deleteNote(note: Note): Response<Note> {

        return retrofitInstance.deleteNote(note)

    }

    override suspend fun updateNote(note: Note) : Response<Note> {

        return   retrofitInstance.updateNote(note)
    }

    override suspend fun loginUser(userData: User): Response<LoginResponse> {

            return retrofitInstance.loginUser(userData)

        /*catch(e: Exception) {
            e.message?.let { Log.d("mainActivity", it) }
            Toast.makeText(context, "The server may be down :(", Toast.LENGTH_SHORT).show()
        }
        */



        /*retrofit.loginUser(userData).enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.message?.let { Log.d("mainActivity", it) }
                Toast.makeText(context, "The server may be down :(", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val addedUser = response.body()
                println(addedUser)
                onResult(addedUser!!)
            }
        })*/
    }

}
