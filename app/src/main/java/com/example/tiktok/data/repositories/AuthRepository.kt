package com.example.tiktok.data.repositories

import com.example.tiktok.data.AuthLocalCache

class AuthRepository private constructor(
    private val cache: AuthLocalCache
) {

    companion object {
        const val TAG = "AuthRepository"
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(cache: AuthLocalCache): AuthRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: AuthRepository(cache).also { INSTANCE = it }
            }
    }

}
