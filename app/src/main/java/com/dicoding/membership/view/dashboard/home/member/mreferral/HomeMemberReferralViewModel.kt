package com.dicoding.membership.view.dashboard.home.member.mreferral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.user.model.ReferralToken
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeMemberReferralViewModel @Inject constructor(
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _referralState = MutableStateFlow<ReferralState>(ReferralState.Idle)
    val referralState: StateFlow<ReferralState> = _referralState

    fun createReferralToken(referralToken: String) {
        viewModelScope.launch {
            _referralState.value = ReferralState.Loading

            userUseCase.createReferralToken(referralToken)
                .onSuccess { response ->
                    _referralState.value = ReferralState.Success(response)
                }
                .onFailure { error ->
                    _referralState.value = ReferralState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }

    // Reset state when needed (e.g., when navigating away)
    fun resetState() {
        _referralState.value = ReferralState.Idle
    }

    // Sealed class to represent different states
    sealed class ReferralState {
        object Idle : ReferralState()
        object Loading : ReferralState()
        data class Success(val data: ReferralToken) : ReferralState()
        data class Error(val message: String) : ReferralState()
    }
}