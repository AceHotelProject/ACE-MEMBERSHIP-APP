package com.dicoding.membership.view.dashboard.profile.detail.membershipku

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.user.model.MembershipUser
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailMembershipkuViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val membershipUseCase: MembershipUseCase
): ViewModel(){
    private val _userData = MutableLiveData<Resource<User>>()
    val userData: LiveData<Resource<User>> = _userData

    private val _membershipData = MutableLiveData<Resource<Membership>>()
    val membershipData: LiveData<Resource<Membership>> = _membershipData

    private val _storeMembershipStatus = MutableLiveData<Resource<Boolean>>()
    val storeMembershipStatus: LiveData<Resource<Boolean>> = _storeMembershipStatus

    // LiveData from Room for observing membership changes
    val activeMembership: LiveData<MembershipLocal?> = membershipUseCase.getActiveMembership()

    private val _subscribeResult = MutableLiveData<Resource<User>>()
    val subscribeResult: LiveData<Resource<User>> = _subscribeResult

    fun subscribe(subscriptionType: String?) {
        viewModelScope.launch {
            // Emit loading state
            _subscribeResult.value = Resource.Loading()

            userUseCase.subscribe(subscriptionType)
                .catch { e ->
                    _subscribeResult.value = Resource.Error(e.message ?: "Failed to renew membership")
                    Log.d("debug", "Error in subscribe: ${e.message}")
                }
                .collect { result ->
                    _subscribeResult.value = result
                }
        }
    }
    // Get user data
    fun getUserData(userId: String) {
        viewModelScope.launch {
            // Emit loading state
            _userData.value = Resource.Loading()

            userUseCase.getUserData(userId)
                .catch { e ->
                    _userData.value = Resource.Error(e.message ?: "Error fetching user data")
                    Log.d("debug", "Error in getUserData")
                }
                .collect { result ->
                    Log.d("Debug View Model", "user ID: $userId")
                    _userData.value = result

                    // If user has active membership, fetch full membership details
                    if (result is Resource.Success && result.data?.isMember == true &&
                        result.data?.isValidated == true) {
                        // Get membership ID from user data
                        val membershipId = result.data!!.membership?.subscriptionType?.id
                        // Fetch full membership details
                        fetchMembership(membershipId?:"empty")
                    }
                }
        }
    }

    // Fetch membership by ID
    fun fetchMembership(membershipId: String) {
        viewModelScope.launch {
            _membershipData.value = Resource.Loading()

            membershipUseCase.getMembershipById(membershipId)
                .catch { e ->
                    _membershipData.value = Resource.Error(e.message ?: "Error fetching membership data")
                    Log.d("debug",  "Error in fetchMembership")
                }
                .collect { result ->
                    _membershipData.value = result

                    // If successfully fetched membership, store it locally
                    if (result is Resource.Success && result.data != null) {
                        storeMembership(result.data)
                    }
                }
        }
    }

    // Store membership data locally
    private fun storeMembership(membership: com.dicoding.core.domain.membership.model.Membership?) {
        viewModelScope.launch {
            _storeMembershipStatus.value = Resource.Loading()

            try {
                val success = membershipUseCase.storeMembershipData(membership!!)
                if (success) {
                    _storeMembershipStatus.value = Resource.Success(true)
                    Log.d("debug", "Successfully stored membership data with images: ${membership.image}")
                } else {
                    _storeMembershipStatus.value = Resource.Error("Failed to store membership data")
                    Log.d("debug", "Failed to store membership data")
                }
            } catch (e: Exception) {
                _storeMembershipStatus.value = Resource.Error(e.message ?: "Error storing membership data")
                Log.d("debug",  "Error in storeMembership")
            }
        }
    }

    // Check if membership is expired
    fun checkMembershipExpiry() {
        viewModelScope.launch {
            val isExpired = membershipUseCase.checkMembershipExpiry()
            if (isExpired) {
                Log.d("debug", "Membership has expired")
            }
        }
    }

    fun updateRemainingCoupons(membershipId: String, remainingCoupons: Int) {
        viewModelScope.launch {
            try {
                val result = membershipUseCase.updateRemainingCoupons(membershipId, remainingCoupons)
                if (!result) {
                    Log.e("debug", "Failed to update remaining coupons")
                }
            } catch (e: Exception) {
                Log.e("debug", "Error updating remaining coupons: ${e.message}")
            }
        }
    }
}