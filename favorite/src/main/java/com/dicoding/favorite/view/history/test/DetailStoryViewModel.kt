package com.dicoding.favorite.view.history.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.story.tester.usecase.StoryUseCaseTester
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import kotlinx.coroutines.launch

class DetailStoryViewModel(
    private val storyUseCase: StoryUseCaseTester
) : ViewModel() {

    val story: LiveData<Resource<StoryDomainTester>> = MutableLiveData<Resource<StoryDomainTester>>()
    val isFavorite: LiveData<Boolean> = MutableLiveData<Boolean>()

    init {
        Log.d("DetailStoryViewModel", "DetailStoryViewModel initialized")
    }

    fun loadStoryDetails(storyId: String) {
        Log.d("DetailStoryViewModel", "loadStoryDetails called with ID: $storyId")
        viewModelScope.launch {
            storyUseCase.getDetailStories(storyId).collect { resource ->
                Log.d("DetailStoryViewModel", "Resource received: $resource")
                (story as MutableLiveData).postValue(resource)
            }
        }
    }

    fun checkIfFavorite(storyId: String) {
        viewModelScope.launch {
            val favoriteStory = storyUseCase.getFavoriteStoryById(storyId)
            (isFavorite as MutableLiveData).postValue(favoriteStory != null)
        }
    }

    fun addFavorite(story: StoryDomainTester) {
        viewModelScope.launch {
            storyUseCase.insertFavoriteStory(story)
            (isFavorite as MutableLiveData).postValue(true)
        }
    }

    fun removeFavorite(story: StoryDomainTester) {
        viewModelScope.launch {
            storyUseCase.deleteFavoriteStory(story)
            (isFavorite as MutableLiveData).postValue(false)
        }
    }
}


