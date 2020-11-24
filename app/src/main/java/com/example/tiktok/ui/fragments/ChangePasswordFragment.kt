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
import androidx.navigation.fragment.findNavController
import com.example.tiktok.R
import com.example.tiktok.databinding.FragmentChangePasswordBinding
import com.example.tiktok.ui.activities.MainActivity
import com.example.tiktok.ui.viewModels.ChangePasswordViewModel
import com.opinyour.android.app.data.utils.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class ChangePasswordFragment : Fragment() {
    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_change_password, container, false
        )
        binding.lifecycleOwner = this
        changePasswordViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(ChangePasswordViewModel::class.java)

        binding.model = changePasswordViewModel
        Log.i("ChangePassword", "Init constructor")

        binding.back.setOnClickListener {
            goToAccountSettings()
        }

        binding.changePassword.setOnClickListener {
            changePassword()
        }

        return binding.root
    }

    fun goToAccountSettings() {
        findNavController()
            .navigate(R.id.action_changePassword_to_profile)
    }

    fun changePassword() {

        runBlocking {
            withContext(Dispatchers.IO) {
                if (changePasswordViewModel.changePassword(
                        (activity as MainActivity).sessionManager.getUserDetails()?.get((activity as MainActivity).sessionManager.KEY_NAME)
                            .toString())) {
                    findNavController()
                        .navigate(R.id.action_changePassword_to_profile)
                }
            }
        }
        if (changePasswordViewModel.message != "") {
            Toast.makeText(context, changePasswordViewModel.message, Toast.LENGTH_SHORT).show()
        }
    }
}
