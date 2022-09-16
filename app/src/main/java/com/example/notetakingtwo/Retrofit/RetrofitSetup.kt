package com.example.notetakingtwo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.notetakingtwo.Models.LoginResponse
import com.example.notetakingtwo.Models.Note
import com.example.notetakingtwo.Models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object RetrofitHelper {
    val baseUrl = "http://10.0.2.2:5000"
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build() //convert to java object
    }
}

interface NotesApi {
    @GET("/")
    suspend fun getNotes(@Query("id") id: String): Response<List<Note>>

    @Headers("Content-Type: application/json")
    @POST("/")
    suspend fun createNote(@Body note: Note): Response<Note>

    @Headers("Content-Type: application/json")
    @POST("/register")
    suspend fun registerUser(@Body user: User): Response<User>

    @Headers("Content-Type: application/json")
    @POST("/login")
    suspend fun loginUser(@Body user: User): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @HTTP(method="DELETE", path = "/", hasBody = true)
    suspend fun deleteNote(@Body note: Note): Response<Note>

    @Headers("Content-Type: application/json")
    @HTTP(method="PATCH", path="/", hasBody = true)
    suspend fun updateNote(@Body note: Note): Response<Note>



}



