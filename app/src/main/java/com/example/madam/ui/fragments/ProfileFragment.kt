package com.example.madam.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.madam.R
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.databinding.FragmentProfileBinding
import com.example.madam.ui.activities.ChangePasswordActivity
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.viewModels.ProfileViewModel
import com.opinyour.android.app.data.utils.Injection
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        binding.lifecycleOwner = this
        profileViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(ProfileViewModel::class.java)
        binding.model = profileViewModel
        Log.i("Profile", "Init constructor")

        binding.changePassword.setOnClickListener {
            changePassword()
        }

        binding.logOut.setOnClickListener {
            logOut()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val user: UserItem? = profileViewModel.getLoggedUser()
            if (user != null) {
                binding.emailAddress.text =
                    user.email
                binding.loginName.text =
                    user.username
            } else {
                binding.emailAddress.text = "unknown"
                binding.loginName.text = "unknown"
            }
        }

        return binding.root
    }

    private fun changePassword() {
        (activity as MainActivity).goToActivity(ChangePasswordActivity::class.java)
    }

    private fun logOut() {
        viewLifecycleOwner.lifecycleScope.launch {
            var user: UserItem? = profileViewModel.getLoggedUser()
            if (user != null) {
                profileViewModel.logOut(user)
                (activity as MainActivity).isLogged.value = false
            }
        }
    }
}
