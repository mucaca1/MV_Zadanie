package com.opinyour.android.app.data.api

import android.content.Context
import com.example.madam.data.api.model.ProfileImageResponse
import com.example.madam.data.api.model.UserResponse
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface WebUserApi {
    @POST("upload.php")
    suspend fun register(): Response<UserResponse>

    @Multipart
    @POST("upload.php")
    fun uploadProfilePic(@Part image: MultipartBody.Part, @Part("data") data: RequestBody): Call<ProfileImageResponse>

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

    fun uploadProfilePic(path: String, token: String){
//        val rawFile = File(path)
//        val rawData = AddProfileRequest()
//        rawData.token = token
//        val data = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(rawData))
//        val imageRequest = RequestBody.create(MediaType.parse("image/jpeg"), rawFile)
//        val image = MultipartBody.Part.createFormData("image", rawFile.name, imageRequest)
//        val apiService = retrofit.create(RestApiService::class.java)
//        val loginResponse: Call<ProfileImageResponse> = apiService.uploadProfilePic(image, data)
//        loginResponse.enqueue(object : Callback<ProfileImageResponse> {
//            override fun onFailure(call: Call<ProfileImageResponse>?, t: Throwable?) {
//                _uploadStatus.value = ServerResponse.SERVER_ERROR
//            }
//            override fun onResponse(
//                call: Call<ProfileImageResponse>?,
//                response: Response<ProfileImageResponse>?
//            ) {
//                if(response?.code() == 200){
//                    _uploadStatus.value = ServerResponse.SERVER_SUCCESS
//                    resolveProfilePicture(token)
//                }else{
//                    _uploadStatus.value = ServerResponse.SERVER_ERROR
//                }
//            }
//        })
    }
}