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
import com.dicoding.core.domain.user.model.UserList
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase,
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase
): ViewModel() {
    private val _userData = MutableLiveData<LoginDomain>()
    val userData: LiveData<LoginDomain> = _userData

    private val _subscriptionHistory = MutableLiveData<Resource<SubscriptionHistory>>()
    val subscriptionHistory: LiveData<Resource<SubscriptionHistory>> = _subscriptionHistory

    private val _userList = MutableLiveData<Resource<UserList>>()
    val userList: LiveData<Resource<UserList>> = _userList


    var currentPage = 1
    private var maxPage = 1
    var isLastPage = false

    private var currentSearch: String? = null
    private var currentSubscriptionType: String? = null
    private var currentStartDate: String? = null

    fun getUserData() {
        viewModelScope.launch {
            authUseCase.getUser()
                .collect { loginDomain ->
                    _userData.value = loginDomain
                }
        }
    }

    fun getAllUsers() {
        if (!isLastPage) {
            viewModelScope.launch {
                userUseCase.getAllUsersData(
                    page = currentPage,
                    search = currentSearch,
                    member = true, // Always true as per requirement
                    subscriptionType = currentSubscriptionType,
                    startDate = currentStartDate
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                maxPage = it.totalPages
                                isLastPage = currentPage >= maxPage
                            }
                        }
                        else -> {}
                    }
                    _userList.value = result
                }
            }
        }
    }
}