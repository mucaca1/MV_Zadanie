package com.example.madam.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.databinding.VideoItemBinding
import com.example.madam.utils.autoNotify
import kotlin.properties.Delegates

class VideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<VideoItem> by Delegates.observable(emptyList()) { prop, old, new ->
        autoNotify(old, new) { o, n -> o.id.compareTo(n.id) == 0 }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    class ViewHolder(private var binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: VideoItem) {
            Log.d("riadok", "je $item")
            binding.video = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = VideoItemBinding.inflate(LayoutInflater.from(parent.context))

                return ViewHolder(
                    view
                )
            }
        }
    }

}