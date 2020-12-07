package com.example.madam.ui.viewModels


import androidx.lifecycle.*
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.VideoRepository
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.utils.UserManager
import kotlinx.coroutines.launch
import java.io.File

class VideoViewModel(
    private val repository: VideoRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val apiPrefix = "http://api.mcomputing.eu/mobv/uploads/"

    var userManager: UserManager = UserManager(userRepository)

    val error: MutableLiveData<String> = MutableLiveData()
    val success: MutableLiveData<String> = MutableLiveData()

    val videos: LiveData<List<VideoItem>>
        get() = transform(repository.getVideos())

    fun loadVideos() {
        viewModelScope.launch {
            repository.loadVideos(userManager.getLoggedUser()!!) { error.postValue(it) }
        }
    }

    fun uploadVideo(videoFile: File) {
        viewModelScope.launch {
            userManager.getLoggedUser()?.token?.let { token ->
                repository.addVideo(
                    videoFile,
                    token,
                    { success.postValue(it) },
                    { error.postValue(it) }
                )
            }
        }
    }

    fun hardLogout() {
        userManager.logoutUser()
    }

    private fun transform(videos: LiveData<List<VideoItem>>): LiveData<List<VideoItem>> {
        return Transformations.map(videos) { items ->
            items.map {
                VideoItem(
                    id = it.id,
                    video_url = if (it.video_url.isNotBlank()) {
                        apiPrefix + it.video_url
                    } else {
                        it.video_url
                    },
                    username = it.username,
                    created_at = it.created_at,
                    user_image_url = if (it.user_image_url.isNotBlank()) {
                        apiPrefix + it.user_image_url
                    } else {
                        it.user_image_url
                    }
                )
            }
        }
    }
}
