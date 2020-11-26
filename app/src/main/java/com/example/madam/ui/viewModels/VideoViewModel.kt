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
import kotlinx.coroutines.launch

class VideoViewModel(private val repository: VideoRepository, private val userRepository: UserRepository) : ViewModel() {

    val error: MutableLiveData<String> = MutableLiveData()

    val videos: LiveData<List<VideoItem>>
        get() = repository.getVideos()

    fun loadVideos() {
        viewModelScope.launch {
            repository.loadVideos { error.postValue(it) }
        }
    }

    suspend fun isLogged(): Boolean {
        var user: UserItem? = userRepository.getLoggedUser()
        Log.i("User", user?.email.toString() + " " + user?.profile.toString())
        return user != null
    }

    suspend fun hardLogout() {
        var user: UserItem? = userRepository.getLoggedUser()
        if (user != null)
            userRepository.logOutUser(user)
    }
}
