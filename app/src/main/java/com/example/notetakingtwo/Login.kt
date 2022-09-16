package com.example.notetakingtwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.window.SplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.notetakingtwo.Models.LoginResponse
import com.example.notetakingtwo.Models.User
import com.example.notetakingtwo.Retrofit.NoteRestApiService
import com.example.notetakingtwo.databinding.ActivityLoginBinding
import com.example.notetakingtwo.repositories.UserRepository
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private val userRepository: UserRepository = UserRepository(NoteRestApiService())
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            loginButton.setOnClickListener {
                val api = NoteRestApiService()
                lifecycleScope.launch {
                    val loginResponse: LoginResponse = userRepository.loginUser(User(loginName.text.toString(), password.text.toString(), "", ""))
                   loginResponse.let {
                       if (it.verified.toString().toBoolean()) {
                       it.apply {
                           (application as NoteTakingApplication).loggedUser = it.user
                           println(user)
                       }
                       val intent: Intent = Intent(this@Login, MainActivity::class.java)
                       startActivity(intent)
                   } else {
                       Toast.makeText(
                           applicationContext,
                           "No such account!",
                           Toast.LENGTH_SHORT
                       ).show()
                   } }


                }

            }
            registerButton.setOnClickListener {
                val intent: Intent = Intent(this@Login, Register::class.java)
                startActivity(intent)
            }
        }
    }
}