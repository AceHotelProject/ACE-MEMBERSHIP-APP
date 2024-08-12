package com.dicoding.dynamicfeature.view.dashboard.history.promo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.core.domain.story.tester.usecase.StoryUseCaseTester
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HistoryPromoViewModel @Inject constructor(
    private val storyUseCase: StoryUseCaseTester
) : ViewModel() {

    fun getStories(filterDate: String, isFinished: Boolean): Flow<PagingData<StoryDomainTester>> {
        return storyUseCase.getStories(filterDate, isFinished).cachedIn(viewModelScope)
    }
}
