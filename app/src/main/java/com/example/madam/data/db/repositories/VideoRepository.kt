package com.example.madam.data.db.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.madam.data.api.model.StatusResponse
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.data.localCaches.VideoLocalCache
import com.opinyour.android.app.data.api.WebApi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
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
    fun getVideos(): LiveData<List<VideoItem>> = cache.getVideos()

//    fun getVideos(): LiveData<List<VideoItem>> {
//        val data: LiveData<List<VideoItem>> = MutableLiveData<List<VideoItem>>(listOf(
//
//            VideoItem(
//                "1",
//                "00:00:00, 1st January of 1970",
//                "http://api.mcomputing.eu/mobv/uploads/post-team13_3-1605822738.mp4",
//                "JohnDoe",
//                "https://www.venmond.com/demo/vendroid/img/avatar/big.jpg",
//
//
//                ),
//            VideoItem(
//                "2",
//                "00:00:00, 1st January of 1970",
//                "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
//                "AnotherDoe",
//                "http://api.mcomputing.eu/mobv/uploads/profile-team13_43.png"
//            ),
//            VideoItem(
//                "3",
//                "00:00:00, 1st January of 1970",
//                "http://api.mcomputing.eu/mobv/uploads/post-team13_3-1605822738.mp4",
//                "user6546541",
//                "https://www.clipartmax.com/png/middle/171-1717870_stockvader-predicted-cron-for-may-user-profile-icon-png.png"
//            ),
//            VideoItem(
//                "4",
//                "00:00:00, 1st January of 1970",
//                "http://api.mcomputing.eu/mobv/uploads/post-team13_3-1605822738.mp4",
//                "abcdefgh",
//                "https://cdn2.vectorstock.com/i/1000x1000/41/11/flat-business-woman-user-profile-avatar-icon-vector-4334111.jpg"
//            ),
//        ))
//
//        return data
//    }

    suspend fun addVideo(videoFile: File, token: String, onError: (error: String) -> Unit) {
        val jsonObject = JSONObject()
        jsonObject.put("apikey", WebApi.API_KEY)
        jsonObject.put("token", token)
        val body = jsonObject.toString()
        val data = RequestBody.create(MediaType.parse("application/json"), body)
        val videoRequest = RequestBody.create(MediaType.parse("video/mp4"), videoFile)
        val video = MultipartBody.Part.createFormData("video", videoFile.name, videoRequest)
        val response = WebApi.create().addPost(video, data)

        response.enqueue(object : Callback<StatusResponse> {
            override fun onFailure(call: Call<StatusResponse>?, t: Throwable?) {
                if (t != null) {
                    Log.i("Upload video", "Error " + t.message)
                    onError("Upload video failed")
                }
            }
            override fun onResponse(
                call: Call<StatusResponse>?,
                response: Response<StatusResponse>?
            ) {
                if (response != null) {
                    if (response.code() == 200) {
                        Log.i("Upload video", response.body()?.status.toString())
                        onError("Successful video upload")
                    } else {
                        Log.i("Upload video", "Upload video failed")
                        onError("Upload video failed")
                    }
                }
            }
        })
    }

    fun getVideo(id: String): LiveData<VideoItem> = cache.getVideo(id)

    suspend fun loadVideos(user: UserItem, onError: (error: String) -> Unit) {

        try {
            val jsonObject = JSONObject()
            jsonObject.put("action", "posts")
            jsonObject.put("apikey", WebApi.API_KEY)
            jsonObject.put("token", user.token)
            val body = jsonObject.toString()
            val data = RequestBody.create(MediaType.parse("application/json"), body)
            val response = api.getVideos(data)

            if (response.isSuccessful) {
                response.body()?.let {
                    return cache.addVideos(it.map { item ->
                        VideoItem(
                            id = item.postid,
                            video_url = item.videourl,
                            user_image_url = item.profile,
                            username = item.username,
                            created_at = item.created
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
