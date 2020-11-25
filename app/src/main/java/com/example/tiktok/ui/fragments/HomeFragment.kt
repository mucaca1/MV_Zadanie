package com.example.madam.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.madam.R
import com.example.madam.databinding.FragmentHomeBinding
import com.example.madam.databinding.FragmentLoginBinding
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.viewModels.LoginViewModel
import com.opinyour.android.app.data.utils.Injection


class HomeFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        binding.lifecycleOwner = this
        /*loginViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(LoginViewModel::class.java)*/
        Log.i("Home", "Init constructor")

        if (!(activity as MainActivity).sessionManager.isLoggedIn()) {
            Log.i("Home", "No user logged")
            findNavController()
                .navigate(R.id.action_home_to_login)
        } else {
            Log.i("Home", "User " + ((activity as MainActivity).sessionManager.getUserDetails()?.get((activity as MainActivity).sessionManager.KEY_NAME)
                ?: "unknown") + " is logged")
        }

        binding.accountButton.setOnClickListener{
            goToAccountSettings()
        }

        return binding.root
    }

    fun goToAccountSettings() {
        findNavController()
            .navigate(R.id.action_home_to_profile)
    }
}
