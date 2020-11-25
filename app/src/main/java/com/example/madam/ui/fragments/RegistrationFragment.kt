package com.example.madam.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.madam.R
import com.example.madam.databinding.FragmentRegistrationBinding
import com.example.madam.ui.viewModels.RegistrationViewModel
import com.opinyour.android.app.data.utils.Injection


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

//        userApi = WebUserApi(context)

        return binding.root
    }

    fun register() {
        registrationViewModel.register()

//        userApi.login(registrationViewModel.email.value.toString(), registrationViewModel.login.value.toString(), registrationViewModel.passwordUtils.hash(registrationViewModel.password.value.toString()))
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
