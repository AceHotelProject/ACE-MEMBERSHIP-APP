package com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearchfilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.promo.usecase.PromoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PromoFilterBottomSheetViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    private val _selectedDatePosition = MutableStateFlow(0)
    val selectedDatePosition: StateFlow<Int> = _selectedDatePosition.asStateFlow()

    private val _selectedStatusPosition = MutableStateFlow(0)
    val selectedStatusPosition: StateFlow<Int> = _selectedStatusPosition.asStateFlow()

    private val _selectedCategoryPosition = MutableStateFlow(0)
    val selectedCategoryPosition: StateFlow<Int> = _selectedCategoryPosition.asStateFlow()

    fun setDatePosition(position: Int) {
        _selectedDatePosition.value = position
    }

    fun setStatusPosition(position: Int) {
        _selectedStatusPosition.value = position
    }

    fun setCategoryPosition(position: Int) {
        _selectedCategoryPosition.value = position
    }
}