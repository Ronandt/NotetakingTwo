package com.example.notetakingtwo.repositories

import android.content.Context
import com.example.notetakingtwo.Interfaces.UserApiDataSource
import com.example.notetakingtwo.Models.LoginResponse
import com.example.notetakingtwo.Models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val networkDataSource: UserApiDataSource) {
    suspend fun loginUser(data: User): LoginResponse {
        try {
            return withContext(Dispatchers.IO) {
                return@withContext networkDataSource.loginUser(data).body()!!
            }
        }
        catch (e: Exception) {
            throw Exception("It seems we have a problem with connecting to the server (${e})")
        }
    }
    suspend fun registerUser(data: User): User {
        try {
            return withContext(Dispatchers.IO) {
                return@withContext networkDataSource.registerUser(data).body()!!
            }
        }
        catch (e: Exception) {
            throw Exception("It seems we have a problem with connecting to the server (${e})")
        }
    }
}