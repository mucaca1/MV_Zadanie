package com.example.tiktok.ui.fragments


//import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.tiktok.R
import com.example.tiktok.databinding.FragmentRegistrationBinding
import com.example.tiktok.ui.viewModels.LoginViewModel
import com.example.tiktok.ui.viewModels.RegistrationViewModel
import com.opinyour.android.app.data.utils.Injection

//import com.example.viewmodel.R
//import com.example.viewmodel.databinding.FragmentHomeBinding
//import com.example.viewmodel.ui.viewModels.HomeViewModel


class RegistrationFragment : Fragment() {
    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var binding: FragmentRegistrationBinding

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

        binding.goToLoginFragmentButton.setOnClickListener {view: View ->
            view.findNavController().navigate(R.id.action_registration_to_login)
        }
        return binding.root
    }
}
