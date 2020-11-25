package com.opinyour.android.app.data.api

import android.content.Context
import com.example.madam.data.api.model.VideoResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

interface WebVideoApi {
    @POST("upload.php")
    suspend fun getVideos(): Response<List<VideoResponse>>

    companion object {
        private const val BASE_URL =
            "http://api.mcomputing.eu/mobv/"

        fun create(context: Context): WebVideoApi {

            val client = OkHttpClient.Builder()
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(WebVideoApi::class.java)
        }
    }
}