package com.example.notetakingtwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.window.SplashScreen
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.notetakingtwo.Models.LoginResponse
import com.example.notetakingtwo.Models.User
import com.example.notetakingtwo.Retrofit.NoteRestApiService
import com.example.notetakingtwo.databinding.ActivityLoginBinding
import com.example.notetakingtwo.repositories.UserRepository
import com.example.notetakingtwo.viewmodels.LoginViewModel
import com.example.notetakingtwo.viewmodels.LoginViewModelFactory
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private val userRepository: UserRepository = UserRepository(NoteRestApiService())
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


       viewModel = ViewModelProviders.of(this, LoginViewModelFactory(application)).get(LoginViewModel::class.java)



        binding.apply {
            loginButton.setOnClickListener {
                    viewModel.verifyCredentials(loginName.text.toString(), password.text.toString())
                    println("DFSJFJSDIOOFJISOFJSDIOFJSIOFSJI")
            }
            registerButton.setOnClickListener {
                val intent: Intent = Intent(this@Login, Register::class.java)
                startActivity(intent)
            }
        }
        observeLoginEvent()
    }

    private fun observeLoginEvent() {
        viewModel.loginFromLiveData.observe(this@Login) {
            println(it)
            if (it) {
                val intent: Intent = Intent(this@Login, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

}