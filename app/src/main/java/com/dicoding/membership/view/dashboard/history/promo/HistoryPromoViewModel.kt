package com.dicoding.membership.view.dashboard.history.promo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class HistoryPromoViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val promoUseCase: PromoUseCase
//    private val storyUseCase: StoryUseCaseTester
) : ViewModel() {

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun getUser() = authUseCase.getUser().asLiveData()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPromoHistory(
        promoName: String = "",
        promoCategory: String = "",
        status: String = ""
    ): Flow<PagingData<PromoHistoryDomain>> {
        // Get user role from AuthUseCase
        val userFlow = authUseCase.getUser()

        return userFlow.flatMapLatest { loginDomain ->
            // Determine default status based on user role
            val defaultStatus = when (mapToUserRole(loginDomain.user.role)) {
                UserRole.ADMIN, UserRole.MITRA -> "valid"
                UserRole.RECEPTIONIST -> "draft"
                UserRole.USER -> "redeemed"
                else -> status // Use provided status for other roles
            }

            // Use the determined status in the promoUseCase call
            promoUseCase.getPromoHistory(
                promoName = promoName,
                promoCategory = promoCategory,
                status = if (status.isEmpty()) defaultStatus else status
            )
        }.cachedIn(viewModelScope)
    }

//    fun getStories(filterDate: String, isFinished: Boolean): Flow<PagingData<StoryDomainTester>> {
//        return storyUseCase.getStories(filterDate, isFinished).cachedIn(viewModelScope)
//    }
}
