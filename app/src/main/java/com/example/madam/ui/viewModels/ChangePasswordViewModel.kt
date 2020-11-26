package com.example.madam.ui.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madam.data.api.model.UserResponse
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.model.UserItem

import com.example.madam.utils.PasswordUtils
import com.opinyour.android.app.data.api.WebApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordViewModel(private val userRepository: UserRepository) : ViewModel() {

    var message: MutableLiveData<String> = MutableLiveData()
    var goBack: MutableLiveData<Boolean> = MutableLiveData()

    val oldPassword: MutableLiveData<String> = MutableLiveData()

    val newPassword: MutableLiveData<String> = MutableLiveData()

    val retypeNewPassword: MutableLiveData<String> = MutableLiveData()

    var passwordUtils: PasswordUtils = PasswordUtils()

    suspend fun changePassword() {
        if (newPassword.value.toString().equals(retypeNewPassword.value.toString())) {
            val jsonObject = JSONObject()
            jsonObject.put("action", "password")
            jsonObject.put("apikey", WebApi.API_KEY)
            jsonObject.put("token", userRepository.getLoggedUser()?.token)
            jsonObject.put("oldpassword", passwordUtils.hash(oldPassword.value.toString()))
            jsonObject.put("newpassword", passwordUtils.hash(newPassword.value.toString()))
            val body = jsonObject.toString()
            val data = RequestBody.create(MediaType.parse("application/json"), body)

            var response: Call<UserResponse> = WebApi.create().register(data)
            response.enqueue(object : Callback<UserResponse> {
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.i("fail", t.message.toString())
                    goBack.postValue(false)
                }

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.code() == 200) {

                        runBlocking {
                            withContext(Dispatchers.IO) {
                                userRepository.update(
                                    UserItem(
                                        response.body()?.username.toString(),
                                        response.body()?.email.toString(),
                                        response.body()?.token.toString(),
                                        response.body()?.refresh.toString(),
                                        response.body()?.profile.toString()
                                    )
                                )
                            }
                        }
                        Log.i("success", response.body()?.id.toString())
                        message.postValue("Heslo bolo úspešne zmenené")
                        goBack.postValue(true)
                    } else {
                        Log.i("success", "Bad login params")
                        message.postValue("Zmena hesla neúspešná")
                        goBack.postValue(false)
                    }
                }
            })
        } else {
            goBack.postValue(false)
            message.postValue("Nové heslo sa nezhoduje")
        }
    }
}