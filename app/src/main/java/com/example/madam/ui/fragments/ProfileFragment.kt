package com.example.madam.ui.fragments

import RealPathUtil
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock.sleep
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.madam.R
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.databinding.FragmentProfileBinding
import com.example.madam.ui.activities.ChangePasswordActivity
import com.example.madam.ui.activities.LoginActivity
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.activities.ShowPhotoDetailActivity
import com.example.madam.ui.viewModels.ProfileViewModel
import com.example.madam.utils.CircleTransform
import com.example.madam.utils.PhotoManager
import com.opinyour.android.app.data.utils.Injection
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.util.*


class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private var photoManager: PhotoManager = PhotoManager()

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.N)
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

        val permissions = arrayOf(
            WRITE_EXTERNAL_STORAGE,
            ACCESS_FINE_LOCATION,
            READ_PHONE_STATE,
            SYSTEM_ALERT_WINDOW,
            CAMERA,
            RECORD_AUDIO
        )
        requestPermissions(permissions, REQUEST_PERMISSIONS_OK_CODE)

        binding.changePassword.setOnClickListener {
            changePassword()
        }

        binding.profileImage.setOnClickListener {
            takePhoto()
        }

        profileViewModel.logOutEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity).isLogged.value = false
        })

        profileViewModel.message.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            binding.loadingPanel.visibility = View.GONE
        })

        val user: UserItem? = profileViewModel.userManager.getLoggedUser()
        if (user != null) {
            binding.emailAddress.text =
                user.email
            binding.loginName.text =
                user.username
            setUserProfile(user)
        } else {
            binding.emailAddress.text = "unknown"
            binding.loginName.text = "unknown"
        }

        profileViewModel.reloadUser()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                profileViewModel.userManager.logoutUser()
                (activity as MainActivity).goToActivity(LoginActivity::class.java)
            }
        })
    }

    private fun changePassword() {
        (activity as MainActivity).goToActivity(ChangePasswordActivity::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            context?.packageManager?.let {
                takePictureIntent.resolveActivity(it)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Log.e("save", "BADDD " + ex.message)
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri? = context?.let { it1 ->
                            FileProvider.getUriForFile(
                                it1,
                                "com.example.madam.provider",
                                it
                            )
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            photoManager.currentPhotoPath = absolutePath
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun takePhoto() {
        // Option
        val listItems: Array<String> =
            arrayOf("Show profile picture", "Open camera", "Open gallery", "Delete photo")
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(activity as MainActivity)
        mBuilder.setTitle("Profile picture")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            Log.i("Select", "Index $i")
            when (i) {
                0 -> {
                    (activity as MainActivity).goToActivity(ShowPhotoDetailActivity::class.java)
                }
                1 -> {
                    dispatchTakePictureIntent()
                }
                2 -> {
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
                3 -> {
                    profileViewModel.deleteProfilePic()
                    Picasso.get()
                        .load(R.drawable.user)
                        .resize(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE)
                        .centerCrop().transform(CircleTransform())
                        .into(binding.profileImage)

                }
                else -> { // Note the block
                    print("x is neither 0 nor 3")
                }
            }
            dialogInterface.cancel()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        galleryAddPic()
        var path: String? = null

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            path = photoManager.currentPhotoPath
        } else if ((requestCode == SELECT_PHOTO || requestCode == CAMERA_REQUEST_CODE) && resultCode == AppCompatActivity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                path = context?.let { RealPathUtil.getRealPath(it, uri) }
            }
        }
        if (path != null) {
            binding.loadingPanel.visibility = View.VISIBLE
            profileViewModel.uploadProfilePic(path)
            val imgFile = File(path)
            if (imgFile.exists()) {
                Picasso.get()
                    .load(imgFile)
                    .resize(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE)
                    .centerInside().transform(CircleTransform())
                    .into(binding.profileImage)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS_OK_CODE) {
            if (permissions[0] == WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("Povolene fotky od usera response")
            }
        }
    }

    private fun setUserProfile(user: UserItem?) {
        Glide.with(this)
            .load(R.drawable.user)
            .override(
                PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE
            )
            .circleCrop()
            .into(binding.profileImage)
        if (user != null) {
            if (user.profile != "") {
                Glide.with(this)
                    .load("http://api.mcomputing.eu/mobv/uploads/" + user.profile)
                    .override(
                        PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE
                    )
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .circleCrop()
                    .into(binding.profileImage)
                /*Picasso.get()
                    .load("http://api.mcomputing.eu/mobv/uploads/" + user.profile).memoryPolicy(
                        MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE)
                    .centerCrop().transform(CircleTransform())
                    .into(binding.profileImage)*/
            }
        }

    }

    fun galleryAddPic() {
        val file = File(photoManager.currentPhotoPath)
        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()),
            arrayOf(file.name), null
        )
    }

    companion object {
        const val SELECT_PHOTO = 1
        const val REQUEST_PERMISSIONS_OK_CODE = 0
        const val CAMERA_REQUEST_CODE = 1001
        const val PROFILE_IMAGE_SIZE = 500
    }
}

