package com.example.notetakingtwo.Interfaces

import android.content.Context
import com.example.notetakingtwo.Models.LoginResponse
import com.example.notetakingtwo.Models.User
import retrofit2.Response

interface UserApiDataSource {
  suspend fun loginUser(userData: User) : Response<LoginResponse>
  suspend fun registerUser(userData: User) : Response<User>
}