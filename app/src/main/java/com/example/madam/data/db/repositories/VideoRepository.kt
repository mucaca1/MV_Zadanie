package com.example.madam.data.db.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
//    // TODO overriden for testing purposes
//    fun getVideos(): LiveData<List<VideoItem>> = cache.getVideos()

    fun getVideos(): LiveData<List<VideoItem>> {
        val data: LiveData<List<VideoItem>> = MutableLiveData<List<VideoItem>>(listOf(

            VideoItem(
                "1",
                "00:00:00, 1st January of 1970",
                "http://api.mcomputing.eu/mobv/uploads/post-team13_3-1605822738.mp4",
                "JohnDoe",
                "https://www.venmond.com/demo/vendroid/img/avatar/big.jpg",


                ),
            VideoItem(
                "2",
                "00:00:00, 1st January of 1970",
                "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                "AnotherDoe",
                "http://api.mcomputing.eu/mobv/uploads/profile-team13_43.png"
            ),
            VideoItem(
                "3",
                "00:00:00, 1st January of 1970",
                "http://api.mcomputing.eu/mobv/uploads/post-team13_3-1605822738.mp4",
                "user6546541",
                "https://www.clipartmax.com/png/middle/171-1717870_stockvader-predicted-cron-for-may-user-profile-icon-png.png"
            ),
            VideoItem(
                "4",
                "00:00:00, 1st January of 1970",
                "http://api.mcomputing.eu/mobv/uploads/post-team13_3-1605822738.mp4",
                "abcdefgh",
                "https://cdn2.vectorstock.com/i/1000x1000/41/11/flat-business-woman-user-profile-avatar-icon-vector-4334111.jpg"
            ),
        ))

        return data
    }

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
                            id = item.id,
                            video_url = item.url,
                            user_image_url = item.userImageUrl,
                            username = item.username,
                            created_at = item.createdAt
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
