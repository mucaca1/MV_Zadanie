package com.example.madam.utils

import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.model.UserItem
import kotlinx.coroutines.runBlocking

class UserManager(private val userRepository: UserRepository) {

    fun isLogged() = runBlocking<Boolean> {
        return@runBlocking userRepository.getLoggedUser() != null
    }

    fun logoutUser() = runBlocking {
        userRepository.logoutUser()
    }

    fun loginUser(user: UserItem) = runBlocking {
        userRepository.loginUser(user)
    }

    fun updateUser(user: UserItem) = runBlocking {
        userRepository.update(user)
    }

    fun getLoggedUser() = runBlocking<UserItem?> {
        return@runBlocking userRepository.getLoggedUser()
    }
}