package com.example.tiktok.data.repositories

import androidx.lifecycle.MutableLiveData
import com.example.tiktok.data.UserLocalCache
import com.example.tiktok.data.repositories.model.UserItem

class UserRepository private constructor(
    private val cache: UserLocalCache
) {

    companion object {
        const val TAG = "UserRepository"
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(cache: UserLocalCache): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: UserRepository(cache).also { INSTANCE = it }
            }
    }

    suspend fun getUsers(): List<UserItem> = cache.getAll()

    suspend fun insertUser(wordItem: UserItem) {
        cache.insertAll(wordItem)
    }

    suspend fun isPasswordValid(login: String, pwd: String): Boolean {
        return (cache.findByLogin(login).password == pwd)
    }

    suspend fun findByLogin(login: String): UserItem {
        return cache.findByLogin(login)
    }

}
