package com.opinyour.android.app.data.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.tiktok.data.UserLocalCache
import com.example.tiktok.data.repositories.AppDatabase
import com.example.tiktok.data.repositories.UserRepository

object Injection {

    private fun provideCache(context: Context): UserLocalCache {
        val database = AppDatabase.getInstance(context)
        return UserLocalCache(database.appUserDao())
    }

    fun provideDataRepository(context: Context): UserRepository {
        return UserRepository.getInstance(provideCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository(
                context
            )
        )
    }
}
