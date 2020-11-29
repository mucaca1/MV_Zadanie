package com.example.madam.ui.fragments


import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.madam.R
import com.example.madam.databinding.FragmentVideoRecordBinding


class VideoRecordFragment : Fragment() {
    private lateinit var binding: FragmentVideoRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_video_record, container, false
        )
        binding.lifecycleOwner = this
        Log.i("VideoRecord", "Init constructor")

        requestPermissions(
            arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSIONS_OK_CODE
        )

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS_OK_CODE) {
            if ((permissions[0] == CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                (permissions[1] == WRITE_EXTERNAL_STORAGE && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // TODO: open camera2
                println("Povolene od usera response")
            }
        }
    }

    companion object {
        const val REQUEST_PERMISSIONS_OK_CODE = 101
    }
}
