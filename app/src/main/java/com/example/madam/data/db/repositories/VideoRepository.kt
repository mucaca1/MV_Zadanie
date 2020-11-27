package com.example.madam.data.db.repositories

import androidx.lifecycle.LiveData
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.data.localCaches.VideoLocalCache
import com.opinyour.android.app.data.api.WebApi
import java.net.ConnectException

class VideoRepository private constructor(
    private val api: WebApi,
    private val cache: VideoLocalCache
) {

    companion object {
        const val TAG = "VideoRepository"

        @Volatile
        private var INSTANCE: VideoRepository? = null

        fun getInstance(api: WebApi, cache: VideoLocalCache): VideoRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: VideoRepository(api, cache).also { INSTANCE = it }
            }
    }

    fun getVideos(): LiveData<List<VideoItem>> = cache.getVideos()

    suspend fun addVideo(wordItem: VideoItem) {
        cache.addVideo(wordItem)
    }

    fun getVideo(id: String): LiveData<VideoItem> = cache.getVideo(id)

    suspend fun loadVideos(onError: (error: String) -> Unit) {

        try {
            val response = api.getVideos()
            if (response.isSuccessful) {
                response.body()?.let {
                    return cache.addVideos(it.map { item ->
                        VideoItem(
                            item.id,
                            item.url,
                            item.userImageUrl,
                            item.username,
                            item.creationDate
                        )
                    })
                }
            }

            onError("Load videos failed. Try again later please.")
        } catch (ex: ConnectException) {
            onError("Off-line. Check internet connection.")
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            onError("Oops...Change failed. Try again later please.")
            ex.printStackTrace()
            return
        }
    }
}
