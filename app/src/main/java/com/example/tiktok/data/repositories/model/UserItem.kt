package com.example.tiktok.data.repositories.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserItem(@PrimaryKey val username: String,
                    @ColumnInfo(name = "email") var email : String?,
                    @ColumnInfo(name = "pwd") val password: String?) {
    // code here
}