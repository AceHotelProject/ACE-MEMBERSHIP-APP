package com.dicoding.membership.view.dashboard.history.promo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryPromoViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase
//    private val storyUseCase: StoryUseCaseTester
) : ViewModel() {
    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getPromoHistory() = promoUseCase.getPromoHistory().asLiveData()

//    fun getStories(filterDate: String, isFinished: Boolean): Flow<PagingData<StoryDomainTester>> {
//        return storyUseCase.getStories(filterDate, isFinished).cachedIn(viewModelScope)
//    }
}
