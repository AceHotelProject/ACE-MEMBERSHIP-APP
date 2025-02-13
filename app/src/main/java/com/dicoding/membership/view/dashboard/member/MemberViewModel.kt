package com.dicoding.membership.view.dashboard.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.user.model.UserList
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    private val _userList = MutableLiveData<Resource<UserList>>()
    val userList: LiveData<Resource<UserList>> = _userList

    var currentPage = 1
    private var maxPage = 1
    private var isLastPage = false

    fun loadMoreUsers() {
        if (currentPage < maxPage) {
            currentPage++
            getAllUsers()
        }
    }

    fun getAllUsers() {
        if (!isLastPage) {
            viewModelScope.launch {
                userUseCase.getAllUsersData(currentPage)
                    .collect { result ->
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

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

}