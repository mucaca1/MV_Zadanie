package com.example.madam.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.madam.R
import com.example.madam.databinding.FragmentLoginBinding
import com.example.madam.ui.viewModels.LoginViewModel
import com.opinyour.android.app.data.utils.Injection


class ProfileFragment : Fragment() {
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

//        binding.model = loginViewModel
        Log.i("Profile", "Init constructor")

        return binding.root
    }
}
