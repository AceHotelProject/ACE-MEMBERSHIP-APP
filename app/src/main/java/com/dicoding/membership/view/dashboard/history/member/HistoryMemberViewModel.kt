package com.dicoding.membership.view.dashboard.history.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.membership.model.SubscriptionHistory
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase,
    private val authUseCase: AuthUseCase
): ViewModel() {
    private val _userData = MutableLiveData<LoginDomain>()
    val userData: LiveData<LoginDomain> = _userData

    private val _subscriptionHistory = MutableLiveData<Resource<SubscriptionHistory>>()
    val subscriptionHistory: LiveData<Resource<SubscriptionHistory>> = _subscriptionHistory

    var currentPage = 1
        private set

    fun getUserData() {
        viewModelScope.launch {
            authUseCase.getUser()
                .collect { loginDomain ->
                    _userData.value = loginDomain
                }
        }
    }

    fun getSubscriptionHistory(isRefresh: Boolean = false) {
        if (isRefresh) currentPage = 1

        viewModelScope.launch {
            membershipUseCase.getSubscriptionHistory(currentPage)
                .collect { result ->
                    _subscriptionHistory.value = result
                }
        }
    }

    fun loadNextPage() {
        currentPage++
        getSubscriptionHistory()
    }
}