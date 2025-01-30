package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.model.UserList
import com.dicoding.core.domain.user.usecase.UserUseCase
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.dataclass.DateFilter
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter.MembershipStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PencarianMemberViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val membershipUseCase: MembershipUseCase
) : ViewModel() {
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
        loadMembershipTypes()
    }

    fun loadAllData() {
        if (!isLoadingComplete.value!!) {
            viewModelScope.launch {
                _userList.value = Resource.Loading()
                try {
                    userUseCase.getAllUsersData(currentPage)
                        .collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    result.data?.let { userList ->
                                        maxPage = userList.totalPages
                                        isLastPage = currentPage >= maxPage
                                        _userList.value = Resource.Success(userList)
                                        if (!isLastPage) {
                                            currentPage++
                                            loadAllData()
                                        } else {
                                            _isLoadingComplete.value = true
                                        }
                                    }
                                }
                                is Resource.Error -> {
                                    _userList.value = result
                                    _isLoadingComplete.value = true
                                }
                                is Resource.Loading -> {
                                    // Handle loading state if needed
                                }

                                else -> {}
                            }
                        }
                } catch (e: Exception) {
                    _userList.value = Resource.Error(e.message ?: "Failed to load users")
                    _isLoadingComplete.value = true
                }
            }
        }
    }

    private fun loadMembershipTypes() {
        viewModelScope.launch {
            membershipUseCase.getAllMemberships()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val types = result.data?.results
                                ?.mapNotNull { it.type }
                                ?.distinct()
                                ?: emptyList()
                            _membershipTypes.value = types
                        }
                        is Resource.Error -> {
                            // Handle error
                        }
                        is Resource.Loading -> {
                            // Handle loading
                        }

                        else -> {}
                    }
                }
        }
    }
}