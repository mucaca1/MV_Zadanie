package com.example.madam.ui.viewModels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madam.data.db.repositories.UserRepository
import com.example.madam.data.db.repositories.VideoRepository
import com.example.madam.data.db.repositories.model.UserItem
import com.example.madam.data.db.repositories.model.VideoItem
import com.example.madam.utils.UserManager
import kotlinx.coroutines.launch

class VideoViewModel(
    private val repository: VideoRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var userManager: UserManager = UserManager(userRepository)

    val error: MutableLiveData<String> = MutableLiveData()

    val videos: LiveData<List<VideoItem>>
        get() = repository.getVideos()

    fun loadVideos() {
        viewModelScope.launch {
            repository.loadVideos { error.postValue(it) }
        }
    }

    fun hardLogout() {
        userManager.logoutUser()
    }
}
