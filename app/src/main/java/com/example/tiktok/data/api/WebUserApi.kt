package com.example.tiktok.data.api

import android.content.Context
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok.data.api.model.UserResponse
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.UnsupportedEncodingException


class WebUserApi(context: Context?) {

    val context: Context? = context
    private val url = "http://api.mcomputing.eu/mobv/service.php"
    private val apiKey = "iG5lI6fC3mS4kR9fA7oP5xT0gM5xW6"

    fun registerUser() {

    }

    fun info() {

    }

    fun login(email: String, userName: String, pwd: String) {
        try {
            val queue = Volley.newRequestQueue(context)

            val jsonBody = JSONObject()
            jsonBody.put("action", "register")
            jsonBody.put("apikey", apiKey)
            jsonBody.put("email", email)
            jsonBody.put("username", userName)
            jsonBody.put("password", pwd)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                com.android.volley.Response.Listener { response ->
//                    textView.text = "Response: %s".format(response.toString())
                    var userResponse: UserResponse = UserResponse(
                        response.get("id").toString(),
                        response.get("username").toString(),
                        response.get("email").toString(),
                        response.get("token").toString(),
                        response.get("refresh").toString(),
                        response.get("profile").toString()
                    )
                    Log.i("WebUserApi", "Response: %s".format(response.toString()))
                },
                com.android.volley.Response.ErrorListener { error ->
                    Log.i("WebUserApi", "Response ERROR: %s".format(error.toString()))
                }
            )

            queue.add(jsonObjectRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun refreshToken() {

    }

    fun changePassword() {

    }

    fun addProfileImage() {

    }

    fun deleteProfileImage() {

    }

    fun isUserCreated() {

    }
}