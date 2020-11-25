package com.opinyour.android.app.data.api

import android.content.Context
import com.example.tiktok.data.api.model.UserResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

interface WebUserApi {
    @POST("realestate")
    suspend fun register(): Response<UserResponse>

    companion object {
        private const val BASE_URL =
            "http://api.mcomputing.eu/mobv/service.php"

        fun create(context: Context): WebUserApi {

            val client = OkHttpClient.Builder()
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(WebUserApi::class.java)
        }
    }
}