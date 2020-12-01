package com.example.madam.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.madam.R
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.databinding.FragmentShowPhotoDetailBinding
import com.example.madam.ui.viewModels.ProfileViewModel
import com.opinyour.android.app.data.utils.Injection
import com.squareup.picasso.Picasso
import java.lang.Thread.sleep


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

        profileViewModel.reloadedUser.observe(viewLifecycleOwner, Observer {
            setUserProfile(it)
        })

        profileViewModel.reloadUser()
        return binding.root
    }


    private fun setUserProfile(userI: UserItem?) {
        val user: UserItem? = userI ?: profileViewModel.userManager.getLoggedUser()
        if (user != null) {
            if (user.profile != "") {
                Picasso.get()
                    .load("http://api.mcomputing.eu/mobv/uploads/" + user.profile)
                    .into(binding.profileImage)
            } else {
                Picasso.get()
                    .load(R.drawable.user)
                    .into(binding.profileImage)
            }
        } else {
            Picasso.get()
                .load(R.drawable.user)
                .into(binding.profileImage)
        }
    }


    companion object {
        fun newInstance() = ShowPhotoDetailFragment()
    }
}

