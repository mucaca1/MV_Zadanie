package com.example.tiktok.utils

import androidx.lifecycle.MutableLiveData
import java.security.MessageDigest

object PasswordUtils {

    fun hash(pwd: String): String {
        val bytes = pwd.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}