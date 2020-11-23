package com.example.tiktok.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiktok.data.repositories.UserRepository
import com.example.tiktok.data.repositories.model.UserItem
import com.example.tiktok.utils.PasswordUtils

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

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
                // new user
                Log.i("Registration", "Registracia platna")
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
                Log.i("Registration", "login uz existuje")
                _registrationStatus.postValue(false)
                return false
            }
        } else {
            Log.i("Registration", "Hesla sa nezhoduju")
            _registrationStatus.postValue(false)
            return false
        }

    }

}