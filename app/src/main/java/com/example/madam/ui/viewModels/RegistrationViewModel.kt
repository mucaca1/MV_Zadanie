package com.example.madam.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.madam.data.api.model.UserExists
import com.example.madam.data.api.model.UserResponse
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.utils.PasswordUtils
import com.opinyour.android.app.data.api.WebApi
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

    var message : MutableLiveData<String> =  MutableLiveData()

    private val _registrationStatus: MutableLiveData<Boolean> = MutableLiveData()
    val registrationStatus: LiveData<Boolean>
        get() = _registrationStatus

    val login: MutableLiveData<String> = MutableLiveData()

    val email: MutableLiveData<String> = MutableLiveData()

    val password: MutableLiveData<String> = MutableLiveData()

    val retypePassword: MutableLiveData<String> = MutableLiveData()

    val passwordUtils: PasswordUtils = PasswordUtils()

    fun registration() {
        if (password.value.toString().equals(retypePassword.value.toString())) {
            if (!checkUsername(login.value.toString())) {
                message.setValue("Používateľské meno už existuje")
                return
            }

            val jsonObject = JSONObject()
            jsonObject.put("action", "register")
            jsonObject.put("apikey", WebApi.API_KEY)
            jsonObject.put("email", email.value.toString())
            jsonObject.put("username", login.value.toString())
            jsonObject.put("password", passwordUtils.hash(password.value.toString()))
            val body = jsonObject.toString()
            val data = RequestBody.create(MediaType.parse("application/json"), body)

            val response: Call<UserResponse> = WebApi.create().register(data)
            response.enqueue(object : Callback<UserResponse> {
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.i("fail", t.message.toString())
                }

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.code() == 200) {
                        Log.i("success", response.body()?.id.toString())
                    } else {
                        message.setValue("Používateľské meno už existuje")
                        Log.i("success", "Username exists")
                    }
                }
            })

        } else {
            message.setValue("Heslá sa nezhodujú")
        }
    }

    fun checkUsername(username: String): Boolean {
        val jsonObject = JSONObject()
        jsonObject.put("action", "exists")
        jsonObject.put("apikey", WebApi.API_KEY)
        jsonObject.put("username", username)

        val body = jsonObject.toString()
        val data = RequestBody.create(MediaType.parse("application/json"), body)

        val response: Call<UserExists> = WebApi.create().isUsernameValid(data)
        var unique: Boolean = false
        response.enqueue(object : Callback<UserExists> {
            override fun onFailure(call: Call<UserExists>, t: Throwable) {
                message.setValue("Používateľské meno už existuje")
                Log.i("fail", t.message.toString())
            }
            override fun onResponse(
                call: Call<UserExists>,
                response: Response<UserExists>
            ) {
                if (response.code() == 200) {
                    unique = response.body()?.exists ?: false
                } else {
                    unique = response.body()?.exists ?: false
                    message.setValue("Používateľské meno už existuje")
                    Log.i("success", "Username exists")
                }
            }
        })
        return unique
    }
}