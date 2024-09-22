package com.scifi.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.scifi.storyapp.data.database.StoryDatabase
import com.scifi.storyapp.data.pref.UserModel
import com.scifi.storyapp.data.pref.UserPreference
import com.scifi.storyapp.data.remote.response.ListStoryItem
import com.scifi.storyapp.data.remote.response.UploadResponse
import com.scifi.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody


class StoryRepository private constructor(
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
) {
    fun getStoriesPager(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): List<ListStoryItem> {
        val response = apiService.getStoriesWithLocation()
        return response.listStory
    }

    suspend fun addStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lng: Double?,
    ): UploadResponse {
        val latitude = lat ?: 0.0
        val longitude = lng ?: 0.0
        return apiService.addStory(photo, description, latitude, longitude)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        fun getInstance(
            userPreference: UserPreference,
            storyDatabase: StoryDatabase,
            apiService: ApiService,
        ) = StoryRepository(userPreference, storyDatabase, apiService)
    }
}
