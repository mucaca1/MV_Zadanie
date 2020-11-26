package com.example.madam.data.db.repositories.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserItem(@PrimaryKey val username: String,
                    @ColumnInfo(name = "email") var email : String?,
                    @ColumnInfo(name = "token") var token : String?,
                    @ColumnInfo(name = "refreshToken") var refreshToken : String?,
                    @ColumnInfo(name = "profile") var profile : String?,
                    ) {
}