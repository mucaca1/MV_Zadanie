package com.example.tiktok.ui.viewModels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok.data.repositories.UserRepository
import com.example.tiktok.utils.PasswordUtils

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _login: MutableLiveData<String> = MutableLiveData()
    val login: LiveData<String>
        get() = _login

    private val _password: MutableLiveData<String> = MutableLiveData()
    val password: LiveData<String>
        get() = _password


    lateinit var passwordUtils: PasswordUtils

    suspend fun login() {
        if (login.value != null && password.value != null) {
            if (userRepository.isPasswordValid(
                    login.toString(),
                    passwordUtils.hash(password.toString())
                )
            ) {
                // login success
                Log.i("Log", "Login OK")
            } else {
                // error
                Log.i("Log", "Bad login")
            }
        }
    }
}
