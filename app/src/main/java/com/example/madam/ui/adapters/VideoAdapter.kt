package com.example.madam.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.madam.R
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.databinding.VideoItemBinding
import com.example.madam.utils.autoNotify
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import kotlin.properties.Delegates

class VideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<VideoItem> by Delegates.observable(emptyList()) { prop, old, new ->
        autoNotify(old, new) { o, n -> o.id.compareTo(n.id) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.video_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(private var binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoItem) {
            binding.videoViewItem = item
            binding.executePendingBindings()


        }
    }

    //TODO refactor this shit elsewhere
    companion object {
        val players = ArrayList<Player>()

        fun registerPlayer(p : Player) {
            players.add(p)
        }

        fun startPlayerAt(position: Int) {
            players[position].prepare()
        }

        fun pauseAllPlayers() {
            players.map {
                it.pause()
            }
        }
    }




}