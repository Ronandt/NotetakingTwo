package com.example.notetakingtwo.viewmodels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.notetakingtwo.Models.LoginResponse
import com.example.notetakingtwo.Models.User
import com.example.notetakingtwo.NoteTakingApplication
import com.example.notetakingtwo.Retrofit.NoteRestApiService
import com.example.notetakingtwo.repositories.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application){
    private val userRepository: UserRepository = UserRepository(NoteRestApiService())
    private val _loginVerifiedLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    private val sharedPref = getApplication<NoteTakingApplication>().getSharedPreferences("credentials", Context.MODE_PRIVATE)
    val loginFromLiveData = _loginVerifiedLiveData


    fun verifyBiometric(username: String = "", password: String = "") {
        val biometricsUsername: String? = sharedPref.getString("BiometricsUsername", "")
        val biometricsPassword: String? = sharedPref.getString("BiometricsPassword", "")
        if(((biometricsUsername == "") or (biometricsPassword == "")) and ((username == "") or (password == ""))) {
            Toast.makeText(getApplication(), "You need to bound your biometrics to your account first by entering your username and password first!", Toast.LENGTH_LONG).show()
        } else if (((biometricsUsername != username) or (biometricsPassword != password)) and ((username != "") and (password != ""))){ //register replace


            verifyCredentials(username, password) {
                if(it) {
                    with(sharedPref.edit()) {
                        putString("BiometricsUsername", username)
                        putString("BiometricsPassword", password)
                        apply()
                    }
                }
            }
        }
        else if ((username == "") and (password == "")) {
            verifyCredentials(biometricsUsername!!, biometricsPassword!!)
        }
    }

    fun verifyCredentials(username: String, password: String, callback: ((result: Boolean) -> Unit)? = null){


        viewModelScope.launch {
            val loginResponse: LoginResponse = userRepository.loginUser(User(username, password))
            loginResponse.let {
                _loginVerifiedLiveData.value = it.verified

                if(_loginVerifiedLiveData.value!!) {
                    (getApplication() as NoteTakingApplication).loggedUser = it.user

                } else {
                    Toast.makeText(getApplication(), "No such account!", Toast.LENGTH_SHORT).show()

                }

                if (callback != null) {
                    callback(it.verified)
                }


            }


        }



    }
}

class LoginViewModelFactory(private var application: Application): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(application) as T
    }
}