package com.opinyour.android.app.data.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.tiktok.data.AuthLocalCache
import com.example.tiktok.data.repositories.AuthRepository

object Injection {

    private fun provideCache(context: Context): AuthLocalCache {
//        val database = AppRoomDatabase.getInstance(context)
        return AuthLocalCache()
    }

    fun provideDataRepository(context: Context): AuthRepository {
        return AuthRepository.getInstance(provideCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository(
                context
            )
        )
    }
}
