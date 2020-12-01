package com.example.madam.ui.fragments


import RealPathUtil
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock.sleep
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.madam.R
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.databinding.FragmentProfileBinding
import com.example.madam.databinding.FragmentShowPhotoDetailBinding
import com.example.madam.ui.activities.ChangePasswordActivity
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.viewModels.ProfileViewModel
import com.example.madam.utils.CircleTransform
import com.google.firebase.analytics.FirebaseAnalytics
import com.opinyour.android.app.data.utils.Injection
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.io.*
import java.io.File.separator
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


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

