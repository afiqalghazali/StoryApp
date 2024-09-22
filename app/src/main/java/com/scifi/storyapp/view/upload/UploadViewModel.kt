package com.scifi.storyapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.scifi.storyapp.data.StoryRepository
import com.scifi.storyapp.data.remote.response.UploadResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UploadViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _uploadResponse = MutableLiveData<UploadResponse>()
    val uploadResponse: LiveData<UploadResponse> = _uploadResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun addStory(photo: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?) {
        viewModelScope.launch {
            try {
                val storyList = repository.addStory(photo, description, lat, lon)
                _uploadResponse.value = storyList
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, UploadResponse::class.java)
                _uploadResponse.value = errorBody
            } catch (e: Exception) {
                _uploadResponse.value =
                    UploadResponse(error = true, message = e.message ?: "Unknown error")
            }
        }
    }
}