package com.dicoding.favorite.view.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.core.domain.story.tester.usecase.StoryUseCaseTester
import com.dicoding.favorite.view.history.test.DetailStoryViewModel
import com.dicoding.favorite.view.home.HomeViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val storyUseCase: StoryUseCaseTester
) : ViewModelProvider.Factory {
    init {
        Log.d("ViewModelFactory", "ViewModelFactory initialized with storyUseCase: $storyUseCase")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                Log.d("ViewModelFactory", "Creating HomeViewModel instance")
                HomeViewModel(storyUseCase) as T
            }
            modelClass.isAssignableFrom(DetailStoryViewModel::class.java) -> {
                Log.d("ViewModelFactory", "Creating DetailStoryViewModel instance")
                DetailStoryViewModel(storyUseCase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
