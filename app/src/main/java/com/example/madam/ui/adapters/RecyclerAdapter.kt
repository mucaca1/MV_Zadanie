package com.example.madam.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madam.R
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.databinding.VideoItemBinding
import com.example.madam.generated.callback.OnClickListener
import com.example.madam.ui.fragments.ProfileFragment
import com.example.madam.utils.CircleTransform
import com.example.madam.utils.autoNotify
import com.google.android.exoplayer2.Player
import com.squareup.picasso.Picasso
import java.io.File
import kotlin.properties.Delegates

class RecyclerAdapter(val user: UserItem, val onRemoveButtonClickListener: (VideoItem) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

//    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
//        val position = holder.adapterPosition
//        VideoPlayerBindingAdapter.releaseRecycledPlayers(position)
//        super.onViewRecycled(holder)
//    }


    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private var binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VideoItem) {
            binding.videoViewItem = item
            binding.playerView.setOnClickListener { VideoPlayerBindingAdapter.togglePlayingState(this.adapterPosition) }
            binding.removePostButton.setOnClickListener { onRemoveButtonClickListener(item) }

            if (belongsToCurrentUser(item)) binding.removePostButton.visibility = View.VISIBLE

            binding.executePendingBindings()
        }

        fun belongsToCurrentUser(item: VideoItem): Boolean {
            return item.username.equals(this@RecyclerAdapter.user.username)
        }
    }
}