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

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

    var message: String = ""

    private val _registrationStatus: MutableLiveData<Boolean> = MutableLiveData()
    val registrationStatus: LiveData<Boolean>
        get() = _registrationStatus

    val login: MutableLiveData<String> = MutableLiveData()

    val email: MutableLiveData<String> = MutableLiveData()

    val password: MutableLiveData<String> = MutableLiveData()

    val retypePassword: MutableLiveData<String> = MutableLiveData()

    val passwordUtils: PasswordUtils = PasswordUtils()


    suspend fun registration(): Boolean {
        if (password.value.equals(retypePassword.value)) {
            val user: UserItem = userRepository.findByLogin(login.value.toString())
            if (user == null) {
                if (email.value.toString() == "") {
                    Log.i("Registration", "Email is empty")
                    message = "Emailová adresa nie je vyplnená"
                    return false
                }
                // new user
                Log.i("Registration", "Registracia platna")
                message = ""
                userRepository.insertUser(
                    UserItem(
                        login.value.toString(),
                        email.value.toString(),
                        passwordUtils.hash(password.value.toString())
                    )
                )
                _registrationStatus.postValue(true)
                return true
            } else {
                // login existuje
                message = "Login už existuje"
                Log.i("Registration", "login uz existuje")
                _registrationStatus.postValue(false)
                return false
            }
        } else {
            message = "Heslá sa nezhodujú"
            Log.i("Registration", "Hesla sa nezhoduju")
            _registrationStatus.postValue(false)
            return false
        }

    }

}