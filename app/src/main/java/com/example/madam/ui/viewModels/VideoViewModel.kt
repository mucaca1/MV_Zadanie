package com.example.madam.ui.viewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madam.data.db.repositories.VideoRepository
import com.example.madam.data.db.repositories.model.VideoItem
import kotlinx.coroutines.launch

class VideoViewModel(private val repository: VideoRepository) : ViewModel() {

    val error: MutableLiveData<String> = MutableLiveData()

    val videos: LiveData<List<VideoItem>>
        get() = repository.getVideos()

    fun loadVideos() {
        viewModelScope.launch {
            repository.loadVideos { error.postValue(it) }
        }
    }
}
