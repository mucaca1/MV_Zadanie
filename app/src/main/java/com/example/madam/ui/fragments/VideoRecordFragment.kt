package com.example.madam.ui.fragments

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.camera2.*
import android.media.MediaCodec
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.android.camera.utils.OrientationLiveData
import com.example.android.camera.utils.getPreviewOutputSize
import com.example.madam.BuildConfig
import com.example.madam.R
import com.example.madam.databinding.FragmentVideoRecordBinding
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.viewModels.VideoViewModel
import com.example.madam.ui.views.AutoFitSurfaceView
import com.opinyour.android.app.data.utils.Injection
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_video_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class VideoRecordFragment : Fragment() {
    private lateinit var binding: FragmentVideoRecordBinding
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var previewSize: Size
    private var isBackCamera = true

    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics("0")
    }
    private val outputFile: File by lazy { createFile(requireContext(), "mp4") }
    private val recorderSurface: Surface by lazy {
        val surface = MediaCodec.createPersistentInputSurface()
        createRecorder(surface).apply {
            prepare()
            release()
        }
        surface
    }

    /** Saves the video recording */
    private val recorder: MediaRecorder by lazy { createRecorder(recorderSurface) }
    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)
    private val animationTask: Runnable by lazy {
        Runnable {
            recordVideo.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.accent))
            overlay.setBackgroundResource(R.drawable.recording_background)
        }
    }
    private val removeAnimation: Runnable by lazy {
        Runnable {
            recordVideo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#66BDBDBD"))
            overlay.background = null
        }
    }

    private lateinit var viewFinder: AutoFitSurfaceView
    private lateinit var overlay: View
    private lateinit var session: CameraCaptureSession
    private lateinit var camera: CameraDevice
    private val previewRequest: CaptureRequest by lazy {
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            addTarget(viewFinder.holder.surface)
        }.build()
    }
    private val recordRequest: CaptureRequest by lazy {
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            addTarget(viewFinder.holder.surface)
            addTarget(recorderSurface)
            set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(30, 30))
        }.build()
    }
    private var recordingStartMillis: Long = 0L
    private lateinit var relativeOrientation: OrientationLiveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_video_record, container, false
        )
        binding.lifecycleOwner = this
        videoViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(VideoViewModel::class.java)
        Log.i("VideoRecord", "Init constructor")

        binding.model = videoViewModel

        videoViewModel.error.observe(viewLifecycleOwner, {
            Toasty.error(requireContext(), it, Toast.LENGTH_LONG).show()
        })
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasPermissions(requireContext())) {
            // TODO : open camera
        } else {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).view_main_pager.currentItem = 1
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        overlay = view.findViewById(R.id.overlay)
        viewFinder = view.findViewById(R.id.view_finder)

        viewFinder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) = Unit

            override fun surfaceCreated(holder: SurfaceHolder) {

                // Selects appropriate preview size and configures view finder
                previewSize = getPreviewOutputSize(
                    viewFinder.display, characteristics, SurfaceHolder::class.java
                )
                Log.d(TAG, "View finder size: ${viewFinder.width} x ${viewFinder.height}")
                Log.d(TAG, "Selected preview size: $previewSize")
                viewFinder.setAspectRatio(previewSize.width, previewSize.height)

                // To ensure that size is set, initialize camera in the view's thread
                viewFinder.post { initializeCamera("0") }

                binding.flipCamera.setOnClickListener {
                    isBackCamera = !isBackCamera
                    camera.close()
                    val cameraId = if (isBackCamera) {
                        "0"
                    } else {
                        "1"
                    }
                    Toasty.info(requireContext(), cameraId, Toast.LENGTH_LONG).show()
                    viewFinder.post { initializeCamera(cameraId) }
                }
            }
        })

        // Used to rotate the output media to match device orientation
        relativeOrientation = OrientationLiveData(requireContext(), characteristics).apply {
            observe(viewLifecycleOwner, Observer { orientation ->
                Log.d(TAG, "Orientation changed: $orientation")
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                (activity as MainActivity).view_main_pager.currentItem = 1
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun createRecorder(surface: Surface) = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setOutputFile(outputFile.absolutePath)
        setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE)
        setVideoFrameRate(30)
        setVideoSize(previewSize.width, previewSize.height)
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setInputSurface(surface)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeCamera(cameraId: String) = lifecycleScope.launch(Dispatchers.Main) {

        // Open the selected camera
        camera = openCamera(cameraManager, cameraId, cameraHandler)

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(viewFinder.holder.surface, recorderSurface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)

        // Sends the capture request as frequently as possible until the session is torn down or
        //  session.stopRepeating() is called
        session.setRepeatingRequest(previewRequest, null, cameraHandler)

        // React to user touching the capture button
        recordVideo.setOnTouchListener { view, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> lifecycleScope.launch(Dispatchers.IO) {

                    // Prevents screen rotation during the video recording
                    requireActivity().requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_LOCKED

                    // Start recording repeating requests, which will stop the ongoing preview
                    //  repeating requests without having to explicitly call `session.stopRepeating`
                    session.setRepeatingRequest(recordRequest, null, cameraHandler)

                    // Finalizes recorder setup and starts recording
                    recorder.apply {
                        // Sets output orientation based on current sensor value at start time
                        relativeOrientation.value?.let { setOrientationHint(it) }
                        prepare()
                        start()
                    }
                    recordingStartMillis = System.currentTimeMillis()
                    Log.d(TAG, "Recording started")

                    // Starts recording animation
                    overlay.post(animationTask)
                }

                MotionEvent.ACTION_UP -> lifecycleScope.launch(Dispatchers.IO) {

                    // Unlocks screen rotation after recording finished
                    requireActivity().requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                    // Requires recording of at least MIN_REQUIRED_RECORDING_TIME_MILLIS
                    val elapsedTimeMillis = System.currentTimeMillis() - recordingStartMillis
                    if (elapsedTimeMillis < MIN_REQUIRED_RECORDING_TIME_MILLIS) {
                        delay(MIN_REQUIRED_RECORDING_TIME_MILLIS - elapsedTimeMillis)
                    }

                    Log.d(TAG, "Recording stopped. Output file: $outputFile")
                    recorder.stop()
                    recorder.release()

                    overlay.post(removeAnimation)
                    // Removes recording animation
                    overlay.removeCallbacks(animationTask)
                    overlay.removeCallbacks(removeAnimation)

                    // Broadcasts the media file to the rest of the system
                    MediaScannerConnection.scanFile(
                        view.context, arrayOf(outputFile.absolutePath), null, null
                    )

                    // Launch external activity via intent to play video recorded using our provider
                    startActivityForResult(Intent().apply {
                        action = Intent.ACTION_VIEW
                        type = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(outputFile.extension)
                        val authority = "${BuildConfig.APPLICATION_ID}.provider"
                        data = FileProvider.getUriForFile(view.context, authority, outputFile)
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }, 202)

                    delay(MainActivity.ANIMATION_SLOW_MILLIS)
                }
            }

            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != 202) return
        val dialogClickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        videoViewModel.uploadVideo(outputFile)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Do you want to add a video ?")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w(TAG, "Camera $cameraId has been disconnected")
                requireActivity().finish()
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }

    override fun onStop() {
        super.onStop()
        try {
            camera.close()
        } catch (exc: Throwable) {
            Log.e(TAG, "Error closing camera", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraThread.quitSafely()
        recorder.release()
        recorderSurface.release()
    }

    companion object {

        private const val PERMISSIONS_REQUEST_CODE = 1
        private val PERMISSIONS_REQUIRED = arrayOf(
            WRITE_EXTERNAL_STORAGE,
            CAMERA,
            RECORD_AUDIO
        )
        private val TAG = VideoRecordFragment::class.java.simpleName
        private const val RECORDER_VIDEO_BITRATE: Int = 10_000_000
        private const val MIN_REQUIRED_RECORDING_TIME_MILLIS: Long = 1000L

        private fun createFile(context: Context, extension: String): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.GERMANY)
            return File(context.filesDir, "VID_${sdf.format(Date())}.$extension")
        }

        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
