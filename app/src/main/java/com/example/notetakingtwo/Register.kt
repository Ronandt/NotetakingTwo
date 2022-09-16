package com.example.notetakingtwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.notetakingtwo.Models.User
import com.example.notetakingtwo.Retrofit.NoteRestApiService
import com.example.notetakingtwo.databinding.ActivityRegisterBinding
import com.example.notetakingtwo.repositories.UserRepository
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val userRepository: UserRepository = UserRepository(NoteRestApiService())
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            registerButton.setOnClickListener {
                lifecycleScope.launch {
                    val user = userRepository.registerUser(   User(
                        login.text.toString(),
                        password.text.toString(),
                        email.text.toString()
                    ))

                    user.let {
                        val intent: Intent = Intent(this@Register, Login::class.java)
                        Toast.makeText(
                            applicationContext,
                            "You have registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                        login.text.clear()
                        email.text.clear()
                        password.text.clear()
                        startActivity(intent)
                    }

                }
            }
        }

    }


}