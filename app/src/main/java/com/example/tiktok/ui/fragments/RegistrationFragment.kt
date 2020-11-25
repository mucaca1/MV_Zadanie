package com.example.tiktok.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok.R
import com.example.tiktok.data.api.WebUserApi
import com.example.tiktok.data.api.model.UserResponse
import com.example.tiktok.databinding.FragmentRegistrationBinding
import com.example.tiktok.ui.viewModels.RegistrationViewModel
import com.opinyour.android.app.data.utils.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject


class RegistrationFragment : Fragment() {
    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var userApi: WebUserApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_registration, container, false
        )
        binding.lifecycleOwner = this
        registrationViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(RegistrationViewModel::class.java)
        binding.model = registrationViewModel

        Log.i("Registration", "Init constructor")

        binding.goToLoginFragmentButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_registration_to_login)
        }

        binding.registrationButton.setOnClickListener {
            register()
        }

        userApi = WebUserApi(context)

        return binding.root
    }

    fun register() {
        userApi.login(registrationViewModel.email.value.toString(), registrationViewModel.login.value.toString(), registrationViewModel.passwordUtils.hash(registrationViewModel.password.value.toString()))
        /*runBlocking {
            withContext(Dispatchers.IO) {
                if (registrationViewModel.registration()) {
                    findNavController().navigate(R.id.action_registration_to_login)
                }
            }
        }
        if (registrationViewModel.message != "") {
            Toast.makeText(context, registrationViewModel.message, Toast.LENGTH_SHORT).show()
        }*/
    }
}
