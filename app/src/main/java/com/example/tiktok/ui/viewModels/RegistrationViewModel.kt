package com.example.tiktok.ui.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok.data.repositories.UserRepository
import com.example.tiktok.data.repositories.model.UserItem
import com.example.tiktok.utils.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

    val login: MutableLiveData<String> = MutableLiveData()

    val email: MutableLiveData<String> = MutableLiveData()

    val password: MutableLiveData<String> = MutableLiveData()

    val retypePassword: MutableLiveData<String> = MutableLiveData()

    val passwordUtils: PasswordUtils = PasswordUtils()


    fun registration() {
        runBlocking {
            withContext(Dispatchers.IO) {
                var user: UserItem = userRepository.findByLogin(login.value.toString())
                if (user == null) {
                    // new user
                    Log.i("Registration", "Registracia platna")
                    var user: UserItem = UserItem(
                        login.value.toString(),
                        email.value.toString(),
                        passwordUtils.hash(password.value.toString())
                    )
                    userRepository.insertWord(user)
                } else {
                    // login existuje
                    Log.i("Registration", "login uz existuje")
                }
            }
        }
    }
}