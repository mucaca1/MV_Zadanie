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
import com.example.madam.R
import com.example.madam.databinding.FragmentChangePasswordBinding
import com.example.madam.ui.activities.ChangePasswordActivity
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.viewModels.ChangePasswordViewModel
import com.opinyour.android.app.data.utils.Injection


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

        changePasswordViewModel.message.observe(viewLifecycleOwner, Observer {
            if (!it.equals("")) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        changePasswordViewModel.goBack.observe(viewLifecycleOwner, Observer {
            if (it) {
                goToAccountSettings()
            }
        })

        return binding.root
    }

    private fun goToAccountSettings() {
        (activity as ChangePasswordActivity).goToActivity(MainActivity::class.java)
    }

    companion object {
        fun newInstance() = ChangePasswordFragment()
    }
}
