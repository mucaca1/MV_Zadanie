package com.opinyour.android.app.data.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.madam.data.localCaches.UserLocalCache
import com.example.madam.data.db.repositories.AppDatabase
import com.example.madam.data.db.repositories.UserRepository

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
