package com.dicoding.membership.view.dashboard.history.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.points.model.UserPointHistory
import com.dicoding.core.domain.points.usecase.PointsUseCase
import com.dicoding.core.domain.user.model.UserList
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryMemberViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase
): ViewModel() {
    private val _userData = MutableLiveData<LoginDomain>()
    val userData: LiveData<LoginDomain> = _userData

    private val _userList = MutableLiveData<Resource<UserList>>()
    val userList: LiveData<Resource<UserList>> = _userList

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

    fun getAllUsers(isRefresh: Boolean = false) {
        if (isRefresh) currentPage = 1

        viewModelScope.launch {
            userUseCase.getAllUsersData(currentPage)
                .collect { result ->
                    _userList.value = result
                }
        }
    }

    fun loadNextPage() {
        currentPage++
        getAllUsers()
    }
}