package com.scifi.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.scifi.storyapp.data.StoryRepository
import com.scifi.storyapp.data.pref.UserModel
import com.scifi.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> =
        repository.getStoriesPager().cachedIn(viewModelScope)

    private val _storiesLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesLocation: LiveData<List<ListStoryItem>> get() = _storiesLocation

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val storyList = repository.getStoriesWithLocation()
                _storiesLocation.postValue(storyList)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
