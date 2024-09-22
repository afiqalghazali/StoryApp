package com.scifi.storyapp.di

import android.content.Context
import com.scifi.storyapp.data.AuthRepository
import com.scifi.storyapp.data.StoryRepository
import com.scifi.storyapp.data.database.StoryDatabase
import com.scifi.storyapp.data.pref.UserPreference
import com.scifi.storyapp.data.pref.dataStore
import com.scifi.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun authRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return AuthRepository.getInstance(pref, apiService)
    }

    fun storyRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(pref, database, apiService)
    }
}