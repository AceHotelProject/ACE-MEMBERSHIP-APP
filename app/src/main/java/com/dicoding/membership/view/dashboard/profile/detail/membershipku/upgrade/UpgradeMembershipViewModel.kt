package com.dicoding.membership.view.dashboard.profile.detail.membershipku.upgrade

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpgradeMembershipViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val membershipUseCase: MembershipUseCase

): ViewModel(){
    private val _userData = MutableLiveData<Resource<User>>()
    val userData: LiveData<Resource<User>> = _userData


    fun getUserData(userId: String) {
        viewModelScope.launch {
            // Emit loading state
            _userData.value = Resource.Loading()

            userUseCase.getUserData(userId)
                .catch { e ->
                    _userData.value = Resource.Error(e.message ?: "Nah")
                }
                .collect { result ->
                    Log.d("Debug View Model", "user ID: ${userId}")
                    _userData.value = result
                }
        }
    }

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

    private val _subscribeResult = MutableLiveData<Resource<User>>()
    val subscribeResult: LiveData<Resource<User>> = _subscribeResult

    fun subscribe(subscriptionType: String?) {
        viewModelScope.launch {
            // Emit loading state
            _subscribeResult.value = Resource.Loading()

            userUseCase.subscribe(subscriptionType)
                .catch { e ->
                    _subscribeResult.value = Resource.Error(e.message ?: "Subscription failed")
                }
                .collect { result ->
                    _subscribeResult.value = result
                }
        }
    }
}