package com.opinyour.android.app.data.api

import com.example.madam.data.api.model.UserRegisterResponse
import com.example.madam.data.api.model.VideoResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WebApi {

    // Response typu boolean znamená, NIE JE IMPLEMENTOVANÉ

    // Registracia
    @POST("service.php")
    fun register(@Body data: RequestBody): Call<UserRegisterResponse>

    // Info
    @POST("service.php")
    fun info(@Body data: RequestBody): Call<Boolean>

    // Login
    @POST("service.php")
    fun login(@Body data: RequestBody): Call<Boolean>

    // Refresh token
    @POST("service.php")
    fun refreshToken(@Body data: RequestBody): Call<Boolean>

    // Zmena hesla
    @POST("service.php")
    fun changePassword(@Body data: RequestBody): Call<Boolean>

    // Pridat profilovku
    @Multipart
    @POST("upload.php")
    fun uploadProfilePicture(@Part image: MultipartBody.Part, @Part("data") data: RequestBody): Call<Boolean>

    // Odstanit profilovku
    @POST("service.php")
    fun deleteProfilePicture(@Body data: RequestBody): Call<Boolean>

    // Prispevky
    @POST("upload")
    suspend fun getVideos(): Response<List<VideoResponse>>

    // Pridat prispevok
    @Multipart
    @POST("upload.php")
    fun addPost(@Part image: MultipartBody.Part, @Part("data") data: RequestBody): Call<Boolean>

    // Existuje username
    @POST("service.php")
    fun isUsernameValid(@Body data: RequestBody): Call<Boolean>

    // Vymazat prispevok
    @POST("service.php")
    fun deletePost(@Body data: RequestBody): Call<Boolean>

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