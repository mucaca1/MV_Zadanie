package com.example.madam.ui.adapters

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.madam.R
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.utils.CircleTransform
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.video_item.view.*

// TODO who uses this?
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Picasso.get().load(it).into(imgView)
    }
}

//TODO check this
@BindingAdapter("playerObject")
fun PlayerView.setPlayerObject(item: VideoItem) {
    val trackSelector = DefaultTrackSelector(context)
    //this
    trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())
    val player = SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build()

    player.setMediaItem(MediaItem.fromUri(Uri.parse(item.video_url)))
    player.playWhenReady = true
    player.seekTo(0, 0)
    player.addListener(VideoPlayerEventListener(player))

    this.player_view.player = player

    //TODO extract
    VideoAdapter.registerPlayer(player)

    player.prepare()
}

@BindingAdapter("ownerPhoto")
fun ImageView.setPostOwnerPhoto(item: VideoItem) {
    var imageUri: String =
        if (item.user_image_url.isEmpty()) {
            "drawable://" + R.drawable.user
        } else {
            item.user_image_url
        }

    Picasso.get()
        .load(imageUri)
        .resize(50, 50)
        .centerCrop().transform(CircleTransform())
        .into(this)
}

// TODO this is a stub - data should be saved in DB as DateTime and the conversion happens here
@BindingAdapter("formattedCreatedAt")
fun TextView.setFormattedCreatedAt(item: VideoItem) {
    this.text = item.created_at
}

// replace this with any formatting needed, currently its degraded to be just a setter
@BindingAdapter("formattedUsername")
fun TextView.setFormattedUsername(item: VideoItem) {
    this.text = item.username
}

class VideoPlayerEventListener(val player: SimpleExoPlayer) : Player.EventListener {
    override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_READY) {
            Log.i("STATE_READY", "STATE_READY")

        }
        if (state == Player.STATE_BUFFERING) {
            Log.i("STATE_BUFFERING", "STATE_BUFFERING")
        }
        if (state == Player.STATE_ENDED) {
            Log.i("STATE_ENDED", "STATE_ENDED")
        }
        if (state == Player.STATE_IDLE) {
            Log.i("STATE_IDLE", "STATE_IDLE")
        }
    }

}

