package com.dicoding.membership.view.dashboard.member.listeditmember

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListEditMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase
) : ViewModel() {

    private val _memberships = MutableLiveData<Resource<MembershipListResponse>>()
    val memberships: LiveData<Resource<MembershipListResponse>> = _memberships

    fun getMemberships() {
        viewModelScope.launch {
            Log.d("Debug", "ViewModel: getMemberships called")  // Add this
            membershipUseCase.getAllMemberships()
                .collect { result ->
                    Log.d("Debug", "ViewModel: received result $result")  // Add this
                    _memberships.value = result
                }
        }
    }
}