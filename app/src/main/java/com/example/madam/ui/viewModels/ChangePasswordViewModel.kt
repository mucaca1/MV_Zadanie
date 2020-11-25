package com.example.madam.ui.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.madam.data.repositories.UserRepository
import com.example.madam.data.repositories.model.UserItem
import com.example.madam.utils.PasswordUtils

class ChangePasswordViewModel(private val userRepository: UserRepository) : ViewModel() {

    var message: String = ""

    val oldPassword: MutableLiveData<String> = MutableLiveData()

    val newPassword: MutableLiveData<String> = MutableLiveData()

    val retypeNewPassword: MutableLiveData<String> = MutableLiveData()

    var passwordUtils: PasswordUtils = PasswordUtils()

    suspend fun changePassword(userName: String): Boolean {
        if (userRepository.isPasswordValid(userName, passwordUtils.hash(oldPassword.value.toString()))) {
            if (newPassword.value.toString().equals(retypeNewPassword.value.toString())) {
                var user: UserItem = userRepository.findByLogin(userName)
                if (user != null) {
                    userRepository.removeUser(user)
                    user.password = passwordUtils.hash(newPassword.value.toString())
                    userRepository.insertUser(user)
                    Log.i("Change password", "Password has changed for user " + user.username)
                    message = ""
                    return true
                }
            } else {
                message = "Nové heslá sa nezhodujú"
                Log.i("Change password", "New password does not match with retyped new password")
                return false
            }
        } else {
            message = "Nesprávne staré heslo"
            Log.i("Change password", "Old password does not match")
            return false
        }
        return false
    }
}