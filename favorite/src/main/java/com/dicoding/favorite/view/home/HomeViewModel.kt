package com.dicoding.favorite.view.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.story.tester.usecase.StoryUseCaseTester

class HomeViewModel (
    private val storyUseCase: StoryUseCaseTester
) : ViewModel() {

    init {
        Log.d("DetailStoryViewModel", "DetailStoryViewModel initialized")
    }

    fun getFavoriteStories() = storyUseCase.getFavoriteStories().asLiveData()
}