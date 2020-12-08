package com.example.madam.ui.adapters

import android.net.Uri
import androidx.databinding.BindingAdapter
import com.example.madam.data.db.repositories.model.VideoItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.video_item.view.*


class VideoPlayerBindingAdapter {
    companion object {

        val players = ArrayList<Player>()

        var currentlyPlaying : Player? = null

        fun registerPlayer(p: Player) {
            players.add(p)
        }

        fun releaseRecycledPlayers(index: Int){
            players[index].release()
        }

        fun pauseCurrentlyPlaying(){
            if (currentlyPlaying != null){
                currentlyPlaying?.playWhenReady = false
                currentlyPlaying = null
            }
        }

        fun startPlayingByIndex(index: Int){
            if (!players[index].playWhenReady) {
                pauseCurrentlyPlaying()
                players[index].playWhenReady = true
                currentlyPlaying = players[index]
            }

        }

        fun togglePlayingState(index: Int) {
            if (currentlyPlaying != null) pauseCurrentlyPlaying()
            else startPlayingByIndex(index)
        }

        fun releaseAll() {
            players.map {
                it.release()
            }
        }

        @JvmStatic
        @BindingAdapter("playerObject")
        fun PlayerView.setPlayerObject(item: VideoItem) {
//            val trackSelector = DefaultTrackSelector(context)//
//            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())
//            val player = SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build()
//            player.setMediaItem(MediaItem.fromUri(Uri.parse(item.video_url)))

            val player = SimpleExoPlayer.Builder(context).build()
            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo")).createMediaSource(Uri.parse(item.video_url))

            player.playWhenReady = false
            setKeepContentOnPlayerReset(true)
            player.repeatMode = Player.REPEAT_MODE_ALL

            this.player = player
            registerPlayer(player)

            player.setMediaSource(mediaSource)
            player.prepare()
        }
    }
}

