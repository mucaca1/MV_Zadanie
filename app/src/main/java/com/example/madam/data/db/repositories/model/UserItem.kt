package com.example.madam.data.db.repositories.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserItem(
    @PrimaryKey val username: String,
    var email: String?,
    var token: String?,
    var refreshToken: String?,
    var profile: String?
)