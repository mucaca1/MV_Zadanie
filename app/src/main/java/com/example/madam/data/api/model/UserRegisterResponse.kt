package com.example.madam.data.api.model

data class UserRegisterResponse(
    var id: String,
    var username: String,
    var email: String,
    var token: String,
    var refresh: String,
    var profile: String
)