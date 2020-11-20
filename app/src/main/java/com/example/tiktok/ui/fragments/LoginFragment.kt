package com.example.tiktok.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.tiktok.R
import com.example.tiktok.databinding.FragmentLoginBinding
import com.example.tiktok.ui.viewModels.LoginViewModel
import com.example.tiktok.utils.PasswordUtils
import com.opinyour.android.app.data.utils.Injection


class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        binding.lifecycleOwner = this
        loginViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(LoginViewModel::class.java)

        binding.model = loginViewModel
        Log.i("Login", "Init constructor")
        binding.goToRegistrationFragmentButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(R.id.action_login_to_registration)
        }


        return binding.root
    }
}
