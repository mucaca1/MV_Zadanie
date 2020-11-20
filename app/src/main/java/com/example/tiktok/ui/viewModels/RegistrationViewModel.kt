package com.example.tiktok.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok.data.repositories.UserRepository
import com.example.tiktok.data.repositories.model.UserItem
import com.example.tiktok.utils.PasswordUtils

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _login: MutableLiveData<String> = MutableLiveData()
    val login: LiveData<String>
        get() = _login

    private val _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String>
        get() = _email

    private val _password: MutableLiveData<String> = MutableLiveData()
    val password: LiveData<String>
        get() = _password

    private val _retypePassword: MutableLiveData<String> = MutableLiveData()
    val retypePassword: LiveData<String>
        get() = _retypePassword

    lateinit var passwordUtils: PasswordUtils


    suspend fun registration() {
        if (userRepository.findByLogin(login.toString()) == null) {
            // new user
            Log.i("Registration", "Registracia plata")
            var user: UserItem = UserItem(login.toString(), email.toString(),  passwordUtils.hash(password.toString()))
            userRepository.insertWord(user)
        } else {
            // login existuje
            Log.i("Registration", "login uz existuje")
        }
    }

    fun isUsernameUnique() {

    }
}