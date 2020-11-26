package com.example.madam.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.model.UserItem

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    suspend fun getLoggedUser(): UserItem? {
        return userRepository.getLoggedUser()
    }

    suspend fun logOut(user: UserItem) {
        return userRepository.logOutUser(user)
    }
}
