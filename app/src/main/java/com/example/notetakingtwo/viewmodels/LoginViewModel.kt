package com.example.notetakingtwo.viewmodels

import android.app.Application
import android.media.metrics.Event
import android.widget.Toast
import androidx.lifecycle.*
import com.example.notetakingtwo.Models.LoginResponse
import com.example.notetakingtwo.Models.User
import com.example.notetakingtwo.NoteTakingApplication
import com.example.notetakingtwo.Retrofit.NoteRestApiService
import com.example.notetakingtwo.repositories.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class LoginViewModel(application: Application) : AndroidViewModel(application){
    private val userRepository: UserRepository = UserRepository(NoteRestApiService())
    private val _loginVerifiedLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val loginFromLiveData = _loginVerifiedLiveData






    fun verifyCredentials(username: String, password: String){


        viewModelScope.launch {
            val loginResponse: LoginResponse = userRepository.loginUser(User(username, password))
            loginResponse.let {
                _loginVerifiedLiveData.value = it.verified

                if(_loginVerifiedLiveData.value!!) {
                    (getApplication() as NoteTakingApplication).loggedUser = it.user

                } else {
                    Toast.makeText(getApplication(), "No such account!", Toast.LENGTH_SHORT).show()

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