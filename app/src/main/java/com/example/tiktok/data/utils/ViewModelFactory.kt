package com.opinyour.android.app.data.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tiktok.data.repositories.UserRepository
import com.example.tiktok.ui.viewModels.LoginViewModel
import com.example.tiktok.ui.viewModels.RegistrationViewModel

/**
 * Factory for ViewModels
 */
class ViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }

        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
