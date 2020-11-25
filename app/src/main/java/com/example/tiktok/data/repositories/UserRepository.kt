package com.example.tiktok.data.repositories

import com.example.tiktok.data.UserLocalCache
import com.example.tiktok.data.api.model.UserResponse
import com.example.tiktok.data.repositories.model.UserItem
import com.opinyour.android.app.data.api.WebUserApi
import java.net.ConnectException

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
//        try {
//            val response = api.register()
//            if (response.isSuccessful) {
//                response.body()?.let { it: UserResponse ->
//                    // TODO cast tu user
////                    cache.insertAll(it)
//                }
//            }
//
//            onError("Load videos failed. Try again later please.")
//        } catch (ex: ConnectException) {
//            onError("Off-line. Check internet connection.")
//            ex.printStackTrace()
//            return
//        } catch (ex: Exception) {
//            onError("Oops...Change failed. Try again later please.")
//            ex.printStackTrace()
//            return
//        }
    }

}
