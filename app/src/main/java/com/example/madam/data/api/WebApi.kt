package com.opinyour.android.app.data.api

import com.example.madam.data.api.model.UserRegisterResponse
import com.example.madam.data.api.model.VideoResponse
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface WebApi {

    @POST("upload")
    suspend fun getVideos(): Response<List<VideoResponse>>

    @POST("service.php")
    fun register(@Body data: RequestBody): Call<UserRegisterResponse>

    companion object {
        private const val BASE_URL =
            "http://api.mcomputing.eu/mobv/"

        const val API_KEY = "iG5lI6fC3mS4kR9fA7oP5xT0gM5xW6"

        fun create(): WebApi {

            val client = OkHttpClient.Builder()
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(WebApi::class.java)
        }
    }
}