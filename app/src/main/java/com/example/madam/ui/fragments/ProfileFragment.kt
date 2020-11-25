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
import com.example.madam.databinding.FragmentProfileBinding
import com.example.madam.ui.activities.MainActivity
import com.opinyour.android.app.data.utils.Injection


class ProfileFragment : Fragment() {
    //private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        binding.lifecycleOwner = this
        /*profileViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(ProfileViewModel::class.java)*/
        Log.i("Profile", "Init constructor")

        binding.changePassword.setOnClickListener {
            changePassword()
        }

        binding.back.setOnClickListener {
            goToMenu()
        }

        binding.logOut.setOnClickListener {
            logOut()
        }

        val user: HashMap<String, String?>? = (activity as MainActivity).sessionManager.getUserDetails()

        if (user != null) {
            binding.emailAddress.text = user.get((activity as MainActivity).sessionManager.KEY_EMAIL)
            binding.loginName.text = user.get((activity as MainActivity).sessionManager.KEY_NAME)
        } else {
            binding.emailAddress.text = "unknown"
            binding.loginName.text = "unknown"
        }

        return binding.root
    }

    fun goToMenu() {
        findNavController()
            .navigate(R.id.action_profile_to_home)
    }

    fun changePassword() {
        findNavController()
            .navigate(R.id.action_profile_to_changePassword)
    }

    fun logOut() {
        (activity as MainActivity).sessionManager.logoutUser()
        findNavController()
            .navigate(R.id.action_profile_to_home)
    }
}
