package com.example.madam.ui.viewModels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.madam.R
import com.example.madam.data.api.model.UserResponse
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.utils.PasswordUtils
import com.opinyour.android.app.data.api.WebApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    var message: MutableLiveData<String> = MutableLiveData()

    private val _loginStatus: MutableLiveData<Boolean> = MutableLiveData()
    val loginStatus: LiveData<Boolean>
        get() = _loginStatus

    private val _user: MutableLiveData<UserItem> = MutableLiveData()
    val user: LiveData<UserItem>
        get() = _user

    val login: MutableLiveData<String> = MutableLiveData()

    val password: MutableLiveData<String> = MutableLiveData()

    var passwordUtils: PasswordUtils = PasswordUtils()

    fun login() {
        val jsonObject = JSONObject()
        jsonObject.put("action", "login")
        jsonObject.put("apikey", WebApi.API_KEY)
        jsonObject.put("username", login.value.toString())
        jsonObject.put("password", passwordUtils.hash(password.value.toString()))
        val body = jsonObject.toString()
        val data = RequestBody.create(MediaType.parse("application/json"), body)

        var response: Call<UserResponse> = WebApi.create().register(data)
        response.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.i("fail", t.message.toString())
            }

            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.code() == 200) {
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            userRepository.loginUser(
                                UserItem(
                                    response.body()?.username.toString(),
                                    response.body()?.email.toString(),
                                    response.body()?.token.toString(),
                                    response.body()?.refresh.toString(),
                                    response.body()?.profile.toString()
                                )
                            )
                            message.postValue("Login")
                        }
                    }
                    Log.i("success", response.body()?.id.toString())
                } else {
                    Log.i("success", "Bad login params")
                    message.postValue("Nesprávne prihlasovacie údaje")
                }
            }
        })
    }
}
