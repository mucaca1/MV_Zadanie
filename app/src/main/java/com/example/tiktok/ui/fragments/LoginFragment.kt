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
import com.example.tiktok.R
import com.example.tiktok.data.api.WebUserApi
import com.example.tiktok.data.repositories.model.UserItem
import com.example.tiktok.databinding.FragmentLoginBinding
import com.example.tiktok.ui.activities.MainActivity
import com.example.tiktok.ui.viewModels.LoginViewModel
import com.opinyour.android.app.data.utils.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.math.log


class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userApi: WebUserApi

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
        binding.loginButton.setOnClickListener {
            login()
        }

        userApi = WebUserApi(context)

        return binding.root
    }

    private fun login() {
        runBlocking {
            withContext(Dispatchers.IO) {
                var user: UserItem? = loginViewModel.login()
                if (user != null) {
                    (activity as MainActivity).sessionManager.createLoginSession(
                        loginViewModel.login.value,
                        user.email
                    )
                    findNavController()
                        .navigate(R.id.action_login_to_home)
                }
            }
        }
        if (loginViewModel.message != "") {
            Toast.makeText(context, loginViewModel.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
//        requestQueue?.cancelAll(TAG)
    }
}
