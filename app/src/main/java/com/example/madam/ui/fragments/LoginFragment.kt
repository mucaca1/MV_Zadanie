package com.example.madam.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.madam.R
import com.example.madam.databinding.FragmentLoginBinding
import com.example.madam.ui.activities.LoginActivity
import com.example.madam.ui.viewModels.LoginViewModel
import com.opinyour.android.app.data.utils.Injection
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch


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
            (activity as LoginActivity).view_login_pager.currentItem = 1
        }

        loginViewModel.message.observe(viewLifecycleOwner, Observer {
            if (it.equals("Login")) {
                (activity as LoginActivity).isLogged.value = true
            } else if (!it.equals("")) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            (activity as LoginActivity).isLogged.value = loginViewModel.isLogged()
        }
    }
}
