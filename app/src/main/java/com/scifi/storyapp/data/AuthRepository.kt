package com.scifi.storyapp.data

import com.scifi.storyapp.data.pref.UserModel
import com.scifi.storyapp.data.pref.UserPreference
import com.scifi.storyapp.data.remote.response.LoginResponse
import com.scifi.storyapp.data.remote.response.RegisterResponse
import com.scifi.storyapp.data.remote.retrofit.ApiService

class AuthRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    companion object {
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ) = AuthRepository(userPreference, apiService)
    }
}