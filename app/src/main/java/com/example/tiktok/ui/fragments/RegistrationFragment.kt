package com.example.tiktok.ui.fragments


//import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.tiktok.R
import com.example.tiktok.databinding.FragmentRegistrationBinding

//import com.example.viewmodel.R
//import com.example.viewmodel.databinding.FragmentHomeBinding
//import com.example.viewmodel.ui.viewModels.HomeViewModel


class RegistrationFragment : Fragment() {
//    private val homeViewModel: HomeViewModel by viewModels()
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
//        binding.model = homeViewModel
        Log.i("Registration", "Init constructor")

        binding.goToLoginFragmentButton.setOnClickListener {view: View ->
            view.findNavController().navigate(R.id.action_registration_to_login)
        }
        return binding.root
    }
}
