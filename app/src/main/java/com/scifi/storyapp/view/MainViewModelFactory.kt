package com.scifi.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.scifi.storyapp.data.StoryRepository
import com.scifi.storyapp.di.Injection
import com.scifi.storyapp.view.main.MainViewModel
import com.scifi.storyapp.view.upload.UploadViewModel

class MainViewModelFactory(private val repository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context): MainViewModelFactory {
            val repository = Injection.storyRepository(context)
            return MainViewModelFactory(repository)
        }
    }
}