package com.example.madam.data.db.repositories.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserItem(@PrimaryKey val username: String,
                    @ColumnInfo(name = "email") var email : String?,
                    @ColumnInfo(name = "pwd") var password: String?) {
    // code here
}