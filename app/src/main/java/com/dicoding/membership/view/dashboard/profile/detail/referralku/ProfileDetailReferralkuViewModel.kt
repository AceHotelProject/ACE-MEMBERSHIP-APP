package com.dicoding.membership.view.dashboard.profile.detail.referralku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.user.model.ReferralToken
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileDetailReferralkuViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {
    private val _referralToken = MutableStateFlow<Result<ReferralToken>?>(null)
    val referralToken: StateFlow<Result<ReferralToken>?> = _referralToken.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getReferralToken() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _referralToken.value = userUseCase.getReferralToken()
            } finally {
                _isLoading.value = false
            }
        }
    }
}