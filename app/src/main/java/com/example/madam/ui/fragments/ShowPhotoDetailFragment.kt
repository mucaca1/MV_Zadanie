package com.example.madam.ui.fragments


import android.os.Bundle
import android.os.SystemClock.sleep
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
import com.example.madam.databinding.FragmentShowPhotoDetailBinding
import com.example.madam.ui.viewModels.ProfileViewModel
import com.opinyour.android.app.data.utils.Injection
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch


class ShowPhotoDetailFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentShowPhotoDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_show_photo_detail, container, false
        )
        profileViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(ProfileViewModel::class.java)
        binding.lifecycleOwner = this
        Log.i("Photo detail", "Init constructor")
        profileViewModel.reloadUser()
        sleep(100)
        viewLifecycleOwner.lifecycleScope.launch {
            val user: UserItem? = profileViewModel.userManager.getLoggedUser()
            if (user != null) {
                setUserProfile()
            }
        }

        return binding.root
    }

    private fun setUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            val user: UserItem? = profileViewModel.userManager.getLoggedUser()
            if (user != null) {
                if (user.profile != "") {
                    Picasso.get()
                        .load("http://api.mcomputing.eu/mobv/uploads/" + user.profile)
                        .into(binding.profileImage)
                }
            } else {
                Picasso.get()
                    .load(R.drawable.user)
                    .into(binding.profileImage)
            }
        }
    }

    companion object {
        fun newInstance() = ShowPhotoDetailFragment()
    }
}

