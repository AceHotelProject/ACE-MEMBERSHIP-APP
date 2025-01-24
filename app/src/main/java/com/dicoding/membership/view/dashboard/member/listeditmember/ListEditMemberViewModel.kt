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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListEditMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase
) : ViewModel() {

    private val _memberships = MutableLiveData<Resource<MembershipListResponse>>()
    val memberships: LiveData<Resource<MembershipListResponse>> = _memberships
    private val _deleteState = MutableLiveData<Resource<Unit>>()
    val deleteState: LiveData<Resource<Unit>> = _deleteState

    fun deleteMembership(id: String) {
        viewModelScope.launch {
            membershipUseCase.deleteMembership(id)
                .onStart {
                    _deleteState.value = Resource.Loading()
                }
                .catch { e ->
                    _deleteState.value = Resource.Error(e.message ?: "An error occurred")
                }
                .collect { result ->
                    _deleteState.value = result
                    if (result is Resource.Success) {
                        // Refresh the membership list after successful deletion
                        getMemberships()
                    }
                }
        }
    }

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