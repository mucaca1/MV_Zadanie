package com.example.madam.ui.fragments


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.example.madam.R
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.databinding.FragmentHomeBinding
import com.example.madam.ui.activities.MainActivity
import com.example.madam.ui.adapters.VideoAdapter
import com.example.madam.ui.viewModels.VideoViewModel
import com.opinyour.android.app.data.utils.Injection
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        binding.lifecycleOwner = this
        videoViewModel =
            ViewModelProvider(this, Injection.provideViewModelFactory(requireContext()))
                .get(VideoViewModel::class.java)
        Log.i("Home", "Init constructor")

        binding.model = videoViewModel

        val list = listOf(
            VideoItem(
                "1",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Janko Mrkvička",
                "22.12.2020 15:48"
            ),
            VideoItem(
                "3",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Matej Kováč",
                "22.12.4565 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            ),
            VideoItem(
                "2",
                "",
                R.drawable.ic_launcher_foreground.toString(),
                "Daniel Vaník",
                "22.12.1199 15:48"
            )
        )

        binding.videoList.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val adapter = VideoAdapter()
        binding.videoList.adapter = adapter
        videoViewModel.videos.observe(viewLifecycleOwner) {
            adapter.items = list
        }

        binding.videoList.addOnScrollListener(createListener())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            (activity as MainActivity).isLogged.value = videoViewModel.userManager.isLogged()
        }
    }

    private fun createListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_START
                    }

                    override fun onStop() {
                        // TODO: play current video post
                        println("Spusti nove video")
                    }

                    override fun calculateTimeForScrolling(dx: Int): Int {
                        val proportion: Float = dx.toFloat()
                        return (0.35 * proportion).toInt()
                    }
                }

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // TODO: stop previous video post
                    println("Zastav aktualne video")
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    var positionToScroll = 0
                    val llm = binding.videoList.layoutManager as LinearLayoutManager
                    if (SCROLLED_UP != null && SCROLLED_UP == true) {
                        positionToScroll = llm.findFirstVisibleItemPosition()
                    } else if (SCROLLED_UP != null && SCROLLED_UP == false) {
                        positionToScroll = llm.findLastVisibleItemPosition()
                    }
                    smoothScroller.targetPosition = positionToScroll
                    binding.videoList.layoutManager?.startSmoothScroll(smoothScroller)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    println("Scrolled Downwards")
                    SCROLLED_UP = false
                } else if (dy < 0) {
                    println("Scrolled Upwards")
                    SCROLLED_UP = true
                }
            }
        }
    }

    companion object {
        private var SCROLLED_UP: Boolean? = null
    }
}