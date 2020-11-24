package com.example.tiktok.ui.viewModels


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok.data.repositories.UserRepository
import com.example.tiktok.data.repositories.model.UserItem
import com.example.tiktok.utils.PasswordUtils
import com.example.tiktok.utils.SessionManager

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    var message: String = ""

    private val _loginStatus: MutableLiveData<Boolean> = MutableLiveData()
    val loginStatus: LiveData<Boolean>
        get() = _loginStatus

    private val _user: MutableLiveData<UserItem> = MutableLiveData()
    val user: LiveData<UserItem>
        get() = _user

    val login: MutableLiveData<String> = MutableLiveData()

    val password: MutableLiveData<String> = MutableLiveData()

    var passwordUtils: PasswordUtils = PasswordUtils()

    lateinit var sessionManager: SessionManager

    suspend fun login(): UserItem? {
        if (login.value != null && password.value != null) {

            if (userRepository.isPasswordValid(
                    login.value.toString(),
                    passwordUtils.hash(password.value.toString())
                )
            ) {
                // login success
                Log.i("Log", "Login OK")
                message = ""
                _user.postValue(userRepository.findByLogin(login.value.toString()))
                _loginStatus.postValue(true)
                return userRepository.findByLogin(login.value.toString())
            } else {
                // error
                Log.i("Log", "Bad login")
                message = "Zlé prihlasovacie údaje"
                _loginStatus.postValue(false)
                return null
            }
        }
        return null
    }
}
