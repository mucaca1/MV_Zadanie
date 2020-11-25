package com.example.madam.data.api.model

data class UserResponse(
    val id: String,
    val userName: String,
    val email: String,
    val token: String,
    val refreshToken: String,
    val profile: String
)