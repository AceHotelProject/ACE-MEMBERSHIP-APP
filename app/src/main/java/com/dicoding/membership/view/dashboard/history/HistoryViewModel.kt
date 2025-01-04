package com.dicoding.membership.view.dashboard.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.core.domain.test.story.usecase.StoryUseCaseTester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val storyUseCase: StoryUseCaseTester,
    private val authUseCase: AuthUseCase,
) : ViewModel() {

    fun getStories(filterDate: String, isFinished: Boolean): Flow<PagingData<StoryDomainTester>> {
        return storyUseCase.getStories(filterDate, isFinished).cachedIn(viewModelScope)
    }

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()
}