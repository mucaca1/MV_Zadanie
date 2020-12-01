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
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.example.madam.ui.activities.ChangePasswordActivity
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.viewModels.ProfileViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.opinyour.android.app.data.utils.Injection
import kotlinx.coroutines.launch
import java.io.*
import java.io.File.separator
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    private lateinit var imageUri: Uri

    lateinit var currentPhotoPath: String

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
            takePhoto()
        }

        val b: Bundle = Bundle()
        b.putString("ErrorMessage", "Something wrong.")
        context?.let { FirebaseAnalytics.getInstance(it).logEvent("ErrorMessage", b) }

        viewLifecycleOwner.lifecycleScope.launch {
            val user: UserItem? = profileViewModel.getLoggedUser()
            if (user != null) {
                binding.emailAddress.text =
                    user.email
                binding.loginName.text =
                    user.username
                setUserProfile()
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
        // Option
        val listItems: Array<String> = arrayOf("Open camera", "Open gallery", "Delete photo")
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(activity as MainActivity)
        mBuilder.setTitle("Profile picture")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            Log.i("Select", "Index " + i.toString())
            when (i) {
                0 -> {
                    Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                    ).also { pickContactIntent ->
                        startActivityForResult(
                            pickContactIntent,
                            CAMERA_REQUEST_CODE
                        )
                    }
                }
                1 -> {
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
                2 -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        profileViewModel.deleteProfilePic()

                    }.invokeOnCompletion {
                        profileViewModel.reloadUser()
                        val bp: Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user)
                        binding.profileImage.setImageBitmap(bp)
                    }
                }
                else -> { // Note the block
                    print("x is neither 1 nor 2")
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

    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getContext()?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val file: File = createImageFile()

            val photo = data.extras!!["data"] as Bitmap?
//            imageView.setImageBitmap(photo)
//            knop.setVisibility(Button.VISIBLE)

            val tempUri: Uri? = context?.let { photo?.let { it1 -> saveImage(it1, it, "MaDaM") } }

            val path: String? = context?.let {
                tempUri?.let { it1 ->
                    RealPathUtil.getRealPath(
                        it,
                        it1
                    )
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                profileViewModel.deleteProfilePic()
                if (path != null) {
                    profileViewModel.uploadProfilePic(path)
                }
//            imageView.setImageBitmap(imageBitmap)
            }.invokeOnCompletion {
                profileViewModel.reloadUser()
                if (path != null) {
                    val imgFile = File(path)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        //Drawable d = new BitmapDrawable(getResources(), myBitmap);
                        binding.profileImage.setImageBitmap(myBitmap)
                    }
                }
            }
        }
        else if ((requestCode == SELECT_PHOTO || requestCode == CAMERA_REQUEST_CODE) && resultCode == AppCompatActivity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {

                val path: String? = context?.let { RealPathUtil.getRealPath(it, uri) }
                if (path != null) {
                    profileViewModel.picturePath.value = path
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    if (path != null) {
                        profileViewModel.deleteProfilePic()
                        profileViewModel.uploadProfilePic(path)
                    }
                }.invokeOnCompletion {
                    profileViewModel.reloadUser()

                    if (path != null) {
                        val imgFile = File(path)
                        if (imgFile.exists()) {
                            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
                            binding.profileImage.setImageBitmap(myBitmap)
                        }
                    }

                }
            }
        }
    }

    companion object {
        const val SELECT_PHOTO = 1
        const val CAMERA_REQUEST_CODE = 1001;
    }

    private fun setUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            val user: UserItem? = profileViewModel.getLoggedUser()
            if (user != null) {
                if (user.profile != "") {
                    val bp: Bitmap? =
                        getBitmapFromURL("http://api.mcomputing.eu/mobv/uploads/" + user.profile)
                    if (bp != null) {
                        binding.profileImage.setImageBitmap(bp)
                    }
                } else {
                    val bp: Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user)
                    binding.profileImage.setImageBitmap(bp)
                }
            }
        }
    }

    /// @param folderName can be your app's name
    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String): Uri? {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
                return uri
            }
        } else {
            val directory = File(Environment.DIRECTORY_PICTURES + separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.ALBUM, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                return file.toUri()
            }
        }
        return null
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
