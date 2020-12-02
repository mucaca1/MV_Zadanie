package com.example.madam.ui.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.madam.data.api.model.ClearPhoto
import com.example.madam.data.api.model.UserResponse
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.utils.UserManager
import com.opinyour.android.app.data.api.WebApi
import com.opinyour.android.app.data.api.WebApi.Companion.create
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    var logOutEvent: MutableLiveData<Boolean> = MutableLiveData()
    var reloadedUser: MutableLiveData<UserItem> = MutableLiveData()
    var userManager: UserManager = UserManager(userRepository)

    var message : MutableLiveData<String> =  MutableLiveData()

    fun logOut() {
        userManager.logoutUser()
        logOutEvent.postValue(true)
    }

    fun reloadUser() {
        val user: UserItem? = userManager.getLoggedUser()
        if (user != null) {
            val jsonObject = JSONObject()
            jsonObject.put("action", "userProfile")
            jsonObject.put("apikey", WebApi.API_KEY)
            jsonObject.put("token", user.token)
            val body = jsonObject.toString()
            val data = RequestBody.create(MediaType.parse("application/json"), body)

            var response: Call<UserResponse> = create().info(data)
            response.enqueue(object : Callback<UserResponse> {
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.i("fail", t.message.toString())
                }

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.code() == 200) {
                        user.profile = response.body()?.profile.toString()
                        user.token = response.body()?.token.toString()
                        user.refreshToken = response.body()?.refresh.toString()
                        reloadedUser.value = user
                        userManager.updateUser(user)
                        Log.i("Info", "Info reload success $user")

                    } else {
                        Log.i("Info", "Error")
                    }
                }
            })
        }
    }

    fun deleteProfilePic() {
        val jsonObject = JSONObject()
        jsonObject.put("action", "clearPhoto")
        jsonObject.put("apikey", WebApi.API_KEY)
        jsonObject.put("token", userManager.getLoggedUser()?.token)
        val body = jsonObject.toString()
        val data = RequestBody.create(MediaType.parse("application/json"), body)

        var response: Call<ClearPhoto> = create().deleteProfilePicture(data)
        response.enqueue(object : Callback<ClearPhoto> {
            override fun onFailure(call: Call<ClearPhoto>, t: Throwable) {
                Log.i("fail", t.message.toString())
            }

            override fun onResponse(
                call: Call<ClearPhoto>,
                response: Response<ClearPhoto>
            ) {
                if (response.code() == 200) {
                    Log.i("success", "Profile pis was deleted")
                    message.value = "Profilová fotka bola zmazaná"
                } else {
                    Log.i("success", "Bad login params")
                }
            }
        })
    }

    fun uploadProfilePic(path: String) {
        val file = File(path)
        val jsonObject = JSONObject()
        jsonObject.put("apikey", WebApi.API_KEY)
        jsonObject.put("token", userManager.getLoggedUser()?.token)
        val body = jsonObject.toString()
        val data = RequestBody.create(MediaType.parse("application/json"), body)
        val imageRequest = RequestBody.create(MediaType.parse("image/jpeg"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, imageRequest)
        val response: Call<ClearPhoto> = create().uploadProfilePicture(image, data)
        response.enqueue(object : Callback<ClearPhoto> {
            override fun onFailure(call: Call<ClearPhoto>?, t: Throwable?) {
                if (t != null) {
                    Log.i("ImgERR", "Error " + t.message)
                    message.value = "Nepodarilo sa zmeniť profilovú fotku"
                }
            }

            override fun onResponse(
                call: Call<ClearPhoto>?,
                response: Response<ClearPhoto>?
            ) {
                if (response != null) {
                    if (response.code() == 200) {
                        Log.i("ImgSucc", response.body()?.status.toString())
                        reloadUser()
                        message.value = "Profilová fotka bola zmenená"
                    } else {
                        Log.i("ImgErr", "Chyba")
                        message.value = "Nepodarilo sa zmeniť profilovú fotku"
                    }
                }
            }
        })
    }
}
