package com.example.tiktok.data

import com.example.tiktok.data.repositories.DbUserDao
import com.example.tiktok.data.repositories.model.UserItem

class UserLocalCache(private val userDao: DbUserDao) {

    suspend fun getAll(): List<UserItem> {
        return userDao.getAll()
    }

    suspend fun loadAllByIds(userIds: IntArray): List<UserItem> {
        return userDao.loadAllByIds(userIds)
    }

    suspend fun findByLogin(login: String): UserItem {
        return userDao.findByLogin(login)
    }

    suspend fun insertAll(user: UserItem) {
        return userDao.insertAll(user)
    }

    suspend fun delete(user: UserItem) {
        userDao.delete(user)
    }
}