package com.scifi.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.scifi.storyapp.data.AuthRepository
import com.scifi.storyapp.data.pref.UserModel
import com.scifi.storyapp.data.remote.response.LoginResponse
import com.scifi.storyapp.view.utils.EspressoIdlingResource
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    fun login(email: String, password: String) {
        EspressoIdlingResource.increment()
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                response.loginResult?.token?.let { token ->
                    saveSession(
                        UserModel(email, token, true),
                        response
                    )
                } ?: run {
                    _loginResponse.value = response
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
                _loginResponse.value = errorBody
            } catch (e: Exception) {
                _loginResponse.value =
                    LoginResponse(error = true, message = e.message ?: "Unknown error")
            } finally {
                EspressoIdlingResource.decrement()
            }
        }
    }

    private suspend fun saveSession(user: UserModel, response: LoginResponse) {
        repository.saveSession(user)
        _loginResponse.value = response
    }
}