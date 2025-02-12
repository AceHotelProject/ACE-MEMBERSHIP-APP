package com.dicoding.membership.view.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase
) : ViewModel() {

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getProposalPromos() = promoUseCase.getProposalPromos().asLiveData()

    fun getEmailVerifiedStatus() = authUseCase.getEmailVerifiedStatus().asLiveData()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val promos: Flow<PagingData<PromoDomain>> = _selectedCategory
        .flatMapLatest { category ->
            getPromos(
                category = category,
                status = "active",
                name = ""
            )
        }
        .cachedIn(viewModelScope)

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    private fun getPromos(category: String, status: String, name: String) =
        promoUseCase.getPromos(category, status, name)
}