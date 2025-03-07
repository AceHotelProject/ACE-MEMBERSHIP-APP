package com.dicoding.membership.view.dashboard.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val membershipUseCase: MembershipUseCase // Add MembershipUseCase injection
) : ViewModel() {

    private val _userData = MutableLiveData<LoginDomain>()
    val userData: LiveData<LoginDomain> = _userData

    // Add this property to observe active membership from local database only
    val activeMembership: LiveData<MembershipLocal?> = membershipUseCase.getActiveMembership()

    fun clearMembershipData() = viewModelScope.launch {
        try {
            // Delete all memberships from local database
            val result = membershipUseCase.deleteAllLocalMemberships()
            Log.d("debug","All membership data cleared: $result")
        } catch (e: Exception) {
            Log.d("debug", "Error clearing membership data")
        }
    }

    fun getUser() = authUseCase.getUser().asLiveData()

    fun deleteUser(user: LoginDomain) = viewModelScope.launch {
        authUseCase.deleteUser(user)
    }

    fun deleteAllData() = viewModelScope.launch {
        authUseCase.deleteAllData()
    }

    fun getUserData() {
        viewModelScope.launch {
            authUseCase.getUser()
                .collect { loginDomain ->
                    _userData.value = loginDomain
                }
        }
    }

    private fun getUserId(): String {
        // Implement based on your authentication system
        return "current_user_id"
    }

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()
}