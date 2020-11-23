package com.example.tiktok.data.repositories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tiktok.data.repositories.model.UserItem

@Dao
interface DbUserDao {

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserItem>

    @Query("SELECT * FROM users WHERE username IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<UserItem>

    @Query("SELECT * FROM users WHERE username LIKE :login LIMIT 1")
    suspend fun findByLogin(login: String): UserItem

    @Insert
    suspend fun insertAll(vararg user: UserItem)

    @Delete
    suspend fun delete(user: UserItem)
}