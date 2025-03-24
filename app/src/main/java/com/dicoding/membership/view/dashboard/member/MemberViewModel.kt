package com.dicoding.membership.view.dashboard.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.membership.model.MembershipStats
import com.dicoding.core.domain.membership.model.Subscription
import com.dicoding.core.domain.membership.model.SubscriptionHistory
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.user.model.UserList
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase,
    private val membershipUseCase: MembershipUseCase // Add this

) : ViewModel() {
    private val _userList = MutableLiveData<Resource<UserList>>()
    val userList: LiveData<Resource<UserList>> = _userList

    var currentPage = 1
    private var maxPage = 1
    var isLastPage = false

    fun loadMoreUsers() {
        if (currentPage < maxPage) {
            currentPage++
            getAllUsers()
        }
    }

    // Add these for filter options
    private var currentSearch: String? = null
    private var currentSubscriptionType: String? = null
    private var currentStartDate: String? = null
    // We'll always filter for members only as per requirement
    private val isMember: Boolean = true

    fun resetFilters() {
        currentSearch = null
        currentSubscriptionType = null
        currentStartDate = null
    }

    fun applyFilters(search: String?, subscriptionType: String?, startDate: String?) {
        // Update filter values
        currentSearch = search
        currentSubscriptionType = subscriptionType
        currentStartDate = startDate

        // Reset pagination and load with new filters
        currentPage = 1
        maxPage = 1
        isLastPage = false
        _userList.value = Resource.Loading()
        getAllUsers()
    }

    fun getAllUsers() {
        if (!isLastPage) {
            viewModelScope.launch {
                userUseCase.getAllUsersData(
                    page = currentPage,
                    search = currentSearch,
                    member = isMember, // Always true as per requirement
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
    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()


    private val _subscriptionHistory = MutableLiveData<Resource<SubscriptionHistory>>()
    val subscriptionHistory: LiveData<Resource<SubscriptionHistory>> = _subscriptionHistory

    private val _membershipStats = MutableLiveData<MembershipStats>()
    val membershipStats: LiveData<MembershipStats> = _membershipStats

    // All subscription data collected across pages
    private val allSubscriptions = mutableListOf<Subscription>()

    var subscriptionPage = 1
    private var subscriptionMaxPage = 1
    private var isLoadingMore = false
    private var isSubscriptionLastPage = false

    // Reset subscription data for pull-to-refresh
    fun resetSubscriptionData() {
        // Reset pagination values
        subscriptionPage = 1
        subscriptionMaxPage = 1
        isSubscriptionLastPage = false
        isLoadingMore = false
        allSubscriptions.clear()

        // Set loading state - this will trigger the SwipeRefreshLayout
        _subscriptionHistory.value = Resource.Loading()
    }

    fun getSubscriptionHistory() {
        if (!isSubscriptionLastPage && !isLoadingMore) {
            isLoadingMore = true
            viewModelScope.launch {
                membershipUseCase.getSubscriptionHistory(subscriptionPage)
                    .collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    subscriptionMaxPage = it.totalPages
                                    isSubscriptionLastPage = subscriptionPage >= subscriptionMaxPage

                                    // If it's page 1 (refresh), clear before adding
                                    if (subscriptionPage == 1) {
                                        allSubscriptions.clear()
                                    }

                                    // Add to our full collection
                                    allSubscriptions.addAll(it.results)

                                    // Calculate statistics
                                    calculateMembershipStats()
                                }
                                isLoadingMore = false
                            }
                            is Resource.Error -> {
                                isLoadingMore = false
                            }
                            else -> {}
                        }
                        _subscriptionHistory.value = result
                    }
            }
        }
    }

    fun loadMoreSubscriptions() {
        if (subscriptionPage < subscriptionMaxPage && !isLoadingMore) {
            subscriptionPage++
            getSubscriptionHistory()
        }
    }

    private fun calculateMembershipStats() {
        viewModelScope.launch {
            // Count total active members
            val activeMembers = allSubscriptions.filter { it.status.equals("active", ignoreCase = true) }

            // Count by membership type
            val membershipCounts = activeMembers
                .groupBy { it.subscriptionType }
                .mapValues { it.value.size }
                .toMutableMap()

            // Create stats object
            val stats = MembershipStats(
                totalMembers = activeMembers.size,
                membershipCounts = membershipCounts
            )

            _membershipStats.value = stats
        }
    }

}