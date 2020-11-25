package com.example.tiktok.data.repositories

import androidx.lifecycle.MutableLiveData
import com.example.tiktok.data.UserLocalCache
import com.example.tiktok.data.api.WebUserApi
import com.example.tiktok.data.repositories.model.UserItem
import java.net.ConnectException

class UserRepository private constructor(
    private val api: WebUserApi,
    private val cache: UserLocalCache
) {

    companion object {
        const val TAG = "UserRepository"
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(api: WebUserApi, cache: UserLocalCache): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: UserRepository(api, cache).also { INSTANCE = it }
            }
    }

    suspend fun getUsers(): List<UserItem> = cache.getAll()

    suspend fun insertUser(userItem: UserItem) {
        cache.insertAll(userItem)
    }

    suspend fun isPasswordValid(login: String, pwd: String): Boolean {
        return (cache.findByLogin(login)?.password == pwd)
    }

    suspend fun findByLogin(login: String): UserItem {
        return cache.findByLogin(login)
    }

    suspend fun removeUser(userItem: UserItem) {
        cache.delete(userItem)
    }

    suspend fun register(onError: (error: String) -> Unit) {
        try {
            val response = api.login()
            if (response.isSuccessful) {
                response.body()?.let { it: UserItem ->
                    cache.insertAll(it)
                }
            }

            onError("Load videos failed. Try again later please.")
        } catch (ex: ConnectException) {
            onError("Off-line. Check internet connection.")
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            onError("Oops...Change failed. Try again later please.")
            ex.printStackTrace()
            return
        }
    }

}
