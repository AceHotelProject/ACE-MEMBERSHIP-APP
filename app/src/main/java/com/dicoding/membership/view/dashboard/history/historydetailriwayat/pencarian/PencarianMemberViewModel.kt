package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.membership.model.Subscription
import com.dicoding.core.domain.membership.model.SubscriptionHistory
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.dataclass.DateFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PencarianMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase
) : ViewModel() {

    private val _subscriptionHistory = MutableLiveData<Resource<SubscriptionHistory>>()
    val subscriptionHistory: LiveData<Resource<SubscriptionHistory>> = _subscriptionHistory

    private val _membershipTypes = MutableLiveData<List<String>>()
    val membershipTypes: LiveData<List<String>> = _membershipTypes

    private val _isLoadingComplete = MutableLiveData(false)
    val isLoadingComplete: LiveData<Boolean> = _isLoadingComplete

    var currentPage = 1
    private var maxPage = 1
    private var isLastPage = false

    init {
        loadMembershipTypes()
    }

    fun searchSubscriptions(
        search: String? = null,
        time: String? = null,
        type: String? = null
    ) {
        viewModelScope.launch {
            _subscriptionHistory.value = Resource.Loading()
            currentPage = 1
            isLastPage = false

            membershipUseCase.getSubscriptionHistory(
                page = currentPage,
                limit = 10,
                search = search,
                time = time,
                type = type
            ).collect { result ->
                _subscriptionHistory.value = when (result) {
                    is Resource.Success -> {
                        if (result.data?.results?.isEmpty() == true) {
                            // If results is empty, pass an empty subscription history
                            Resource.Success(SubscriptionHistory(
                                results = emptyList(),
                                page = 1,
                                limit = 10,
                                totalPages = 0,
                                totalResults = 0
                            ))
                        } else {
                            result.data?.let {
                                maxPage = it.totalPages
                                isLastPage = currentPage >= maxPage
                            }
                            result
                        }
                    }
                    else -> result
                }

                _isLoadingComplete.value = true
            }
        }
    }

    fun loadMoreSubscriptions(
        search: String? = null,
        time: String? = null,
        type: String? = null
    ) {
        if (!isLastPage) {
            viewModelScope.launch {
                currentPage++

                membershipUseCase.getSubscriptionHistory(
                    page = currentPage,
                    limit = 10,
                    search = search,
                    time = time,
                    type = type
                ).collect { result ->
                    if (result is Resource.Success) {
                        val currentList = _subscriptionHistory.value?.data?.results ?: emptyList()
                        val newList = result.data?.results ?: emptyList()

                        val combinedList = SubscriptionHistory(
                            results = currentList + newList,
                            page = result.data?.page ?: 1,
                            limit = result.data?.limit ?: 10,
                            totalPages = result.data?.totalPages ?: 1,
                            totalResults = result.data?.totalResults ?: 0
                        )

                        _subscriptionHistory.value = Resource.Success(combinedList)

                        result.data?.let {
                            maxPage = it.totalPages
                            isLastPage = currentPage >= maxPage
                        }
                    }
                }
            }
        }
    }

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