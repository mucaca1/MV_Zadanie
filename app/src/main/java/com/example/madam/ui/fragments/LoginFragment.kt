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
import androidx.navigation.fragment.findNavController
import com.example.madam.R
import com.example.madam.databinding.FragmentLoginBinding
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.viewModels.LoginViewModel
import com.opinyour.android.app.data.utils.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


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
        binding.loginButton.setOnClickListener {
            login()
        }

        return binding.root
    }

    private fun login() {
        runBlocking {
            withContext(Dispatchers.IO) {
                if (loginViewModel.login()) {
                    (activity as MainActivity).sessionManager.createLoginSession(
                        loginViewModel.login.value,
                        loginViewModel.user.value?.email
                    )
                    (activity as MainActivity).pagerAdapter.addAfterSignFragments()
                    (activity as MainActivity).pagerAdapter.notifyDataSetChanged()
                    findNavController()
                        .navigate(R.id.action_login_to_home)
                }
            }
        }
    }
}
