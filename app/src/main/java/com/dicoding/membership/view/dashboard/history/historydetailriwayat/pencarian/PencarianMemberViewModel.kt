package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.model.UserList
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PencarianMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase,
    private val userUseCase: UserUseCase // Add the user use case
) : ViewModel() {

    // Change from SubscriptionHistory to UserList
    private val _userList = MutableLiveData<Resource<UserList>>()
    val userList: LiveData<Resource<UserList>> = _userList

    private val _membershipTypes = MutableLiveData<List<String>>()
    val membershipTypes: LiveData<List<String>> = _membershipTypes

    private val _isLoadingComplete = MutableLiveData(false)
    val isLoadingComplete: LiveData<Boolean> = _isLoadingComplete

    var currentPage = 1
    private var maxPage = 1
    private var isLastPage = false

    init {
        loadMembershipTypes() // Keep this for the membership type filter
    }

    // Change method to search users instead of subscriptions
    fun searchUsers(
        search: String? = null,
        startDate: String? = null,
        subscriptionType: String? = null
    ) {
        viewModelScope.launch {
            _userList.value = Resource.Loading()
            currentPage = 1
            isLastPage = false

            userUseCase.getAllUsersData(
                page = currentPage,
                search = search,
                member = true, // Always true as per requirement
                subscriptionType = subscriptionType,
                startDate = startDate
            ).collect { result ->
                _userList.value = when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            maxPage = it.totalPages
                            isLastPage = currentPage >= maxPage
                        }
                        result
                    }
                    else -> result
                }

                _isLoadingComplete.value = true
            }
        }
    }

    fun loadMoreUsers(
        search: String? = null,
        startDate: String? = null,
        subscriptionType: String? = null
    ) {
        if (!isLastPage) {
            viewModelScope.launch {
                currentPage++

                userUseCase.getAllUsersData(
                    page = currentPage,
                    search = search,
                    member = true,
                    subscriptionType = subscriptionType,
                    startDate = startDate
                ).collect { result ->
                    if (result is Resource.Success) {
                        val currentUsers = _userList.value?.data?.data ?: emptyList()
                        val newUsers = result.data?.data ?: emptyList()

                        val combinedList = UserList(
                            data = currentUsers + newUsers,
                            page = result.data?.page ?: 1,
                            limit = result.data?.limit ?: 10,
                            totalPages = result.data?.totalPages ?: 1,
                            totalResults = result.data?.totalResults ?: 0
                        )

                        _userList.value = Resource.Success(combinedList)

                        result.data?.let {
                            maxPage = it.totalPages
                            isLastPage = currentPage >= maxPage
                        }
                    }
                }
            }
        }
    }

    // Keep this for membership type filters
    private fun loadMembershipTypes() {
        viewModelScope.launch {
            membershipUseCase.getAllMemberships()
                .collect { result ->
                    if (result is Resource.Success) {
                        val types = result.data?.results
                            ?.mapNotNull { it.type }
                            ?.distinct()
                            ?: emptyList()
                        _membershipTypes.value = types
                    }
                }
        }
    }
}