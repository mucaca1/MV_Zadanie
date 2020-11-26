package com.example.madam.ui.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.madam.data.api.model.ProfileImageResponse
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.model.UserItem
import com.opinyour.android.app.data.api.WebApi
import com.opinyour.android.app.data.api.WebApi.Companion.create
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    var picturePath: MutableLiveData<String> = MutableLiveData()

    suspend fun getLoggedUser(): UserItem? {
        return userRepository.getLoggedUser()
    }

    suspend fun updateUser(user: UserItem) {
        userRepository.update(user)
    }

    suspend fun logOut(user: UserItem) {
        return userRepository.logOutUser(user)
    }

    suspend fun uploadProfilePic(path: String) {
        val file = File(path)
        val jsonObject = JSONObject()
        jsonObject.put("apikey", WebApi.API_KEY)
        jsonObject.put("token", userRepository.getLoggedUser()?.token)
        val body = jsonObject.toString()
        val data = RequestBody.create(MediaType.parse("application/json"), body)
        val imageRequest = RequestBody.create(MediaType.parse("image/jpeg"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, imageRequest)
        val response: Call<ProfileImageResponse> = create().uploadProfilePicture(image, data)
        response.enqueue(object : Callback<ProfileImageResponse> {
            override fun onFailure(call: Call<ProfileImageResponse>?, t: Throwable?) {
                // TODO fail
                if (t != null) {
                    Log.i("ImgERR", "Error " + t.message)
                }
            }

            override fun onResponse(
                call: Call<ProfileImageResponse>?,
                response: Response<ProfileImageResponse>?
            ) {
                if (response != null) {
                    if (response.code() == 200) {
                        Log.i("ImgSucc", response.body()?.path.toString())
                    } else {
                        Log.i("ImgSucc", "Chyba")
                    }
                }
            }
        })
    }
}
