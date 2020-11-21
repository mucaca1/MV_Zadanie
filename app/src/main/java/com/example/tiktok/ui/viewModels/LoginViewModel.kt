package com.example.tiktok.ui.viewModels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok.data.repositories.UserRepository
import com.example.tiktok.data.repositories.model.UserItem
import com.example.tiktok.utils.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    val login: MutableLiveData<String> = MutableLiveData()

    val password: MutableLiveData<String> = MutableLiveData()

    var passwordUtils: PasswordUtils = PasswordUtils()

    fun login() {
        runBlocking {
            withContext(Dispatchers.IO) {
                if (login.value != null && password.value != null) {

                    if (userRepository.isPasswordValid(
                            login.value.toString(),
                            passwordUtils.hash(password.value.toString())
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

    }
}
