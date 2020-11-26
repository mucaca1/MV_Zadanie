package com.example.madam.ui.fragments


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

        return binding.root
    }
}
