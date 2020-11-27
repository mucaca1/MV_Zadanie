package com.example.madam.ui.fragments


import RealPathUtil
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    private lateinit var imageUri: Uri

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

        binding.profileImage.setOnClickListener {
            (activity as MainActivity).allowImagePermissions()
            takePhoto()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val user: UserItem? = profileViewModel.getLoggedUser()
            if (user != null) {
                binding.emailAddress.text =
                    user.email
                binding.loginName.text =
                    user.username
//                profileViewModel.picturePath.postValue(user.profile)
                val bp: Bitmap? = getBitmapFromURL("http://api.mcomputing.eu/mobv/uploads/" + user.profile)
                if (bp != null) {
                    binding.profileImage.setImageBitmap(bp)
                } else {
//                    binding.profileImage.setImageBitmap()
//                    val icon = requireContext().packageManager
//                        .getmi
//                    binding.profileImage.setImageDrawable(icon)
                }


            } else {
                binding.emailAddress.text = "unknown"
                binding.loginName.text = "unknown"
            }
        }

        return binding.root
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun changePassword() {
        (activity as MainActivity).goToActivity(ChangePasswordActivity::class.java)
    }

    private fun logOut() {
        viewLifecycleOwner.lifecycleScope.launch {
            val user: UserItem? = profileViewModel.getLoggedUser()
            if (user != null) {
                profileViewModel.logOut(user)
                (activity as MainActivity).isLogged.value = false
            }
        }
    }

    private fun takePhoto() {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ).also { pickContactIntent ->
            startActivityForResult(
                pickContactIntent,
                SELECT_PHOTO
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {

                val path: String? = context?.let { RealPathUtil.getRealPath(it, uri) }
                if (path != null) {
                    profileViewModel.picturePath.value = path
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    val user: UserItem? = profileViewModel.getLoggedUser()
                    if (user != null && path != null) {
                        user.profile = path
                        profileViewModel.updateUser(user)
                    }
                    if (path != null) {
                        profileViewModel.deleteProfilePic()
                        profileViewModel.uploadProfilePic(path)
                    }
                }
            }
        }
    }

    companion object {
        const val SELECT_PHOTO = 1
    }
}
