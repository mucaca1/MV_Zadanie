package com.example.madam.ui.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.madam.data.api.model.UserRegisterResponse
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.utils.PasswordUtils
import com.opinyour.android.app.data.api.WebApi
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

    var message: String = ""

    private val _registrationStatus: MutableLiveData<Boolean> = MutableLiveData()
    val registrationStatus: LiveData<Boolean>
        get() = _registrationStatus

    val login: MutableLiveData<String> = MutableLiveData()

    val email: MutableLiveData<String> = MutableLiveData()

    val password: MutableLiveData<String> = MutableLiveData()

    val retypePassword: MutableLiveData<String> = MutableLiveData()

    val passwordUtils: PasswordUtils = PasswordUtils()

    fun register() {
        val jsonObject = JSONObject()
        jsonObject.put("action", "register")
        jsonObject.put("apikey", WebApi.API_KEY)
        jsonObject.put("email", email.value.toString())
        jsonObject.put("username", login.value.toString())
        jsonObject.put("password", passwordUtils.hash(password.value.toString()))
        val body = jsonObject.toString()
        val data = RequestBody.create(MediaType.parse("application/json"), body)

        var registerResponse: Call<UserRegisterResponse> = WebApi.create().register(data)
        registerResponse.enqueue(object : Callback<UserRegisterResponse> {
            override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                Log.i("fail", t.message.toString())
            }

            override fun onResponse(
                call: Call<UserRegisterResponse>,
                registerResponse: Response<UserRegisterResponse>
            ) {
                if (registerResponse?.code() == 200) {
                    Log.i("success", registerResponse.body()?.id.toString())
                } else {
                    Log.i("success", "Username exists")
                }
            }

        })

    }

    suspend fun registration(): Boolean {
        if (password.value.equals(retypePassword.value)) {
            val user: UserItem = userRepository.findByLogin(login.value.toString())
            if (user == null) {
                if (email.value.toString() == "") {
                    Log.i("Registration", "Email is empty")
                    message = "Emailová adresa nie je vyplnená"
                    return false
                }
                // new user
                Log.i("Registration", "Registracia platna")
                message = ""
                userRepository.insertUser(
                    UserItem(
                        login.value.toString(),
                        email.value.toString(),
                        passwordUtils.hash(password.value.toString())
                    )
                )
                _registrationStatus.postValue(true)
                return true
            } else {
                // login existuje
                message = "Login už existuje"
                Log.i("Registration", "login uz existuje")
                _registrationStatus.postValue(false)
                return false
            }
        } else {
            message = "Heslá sa nezhodujú"
            Log.i("Registration", "Hesla sa nezhoduju")
            _registrationStatus.postValue(false)
            return false
        }

    }

}