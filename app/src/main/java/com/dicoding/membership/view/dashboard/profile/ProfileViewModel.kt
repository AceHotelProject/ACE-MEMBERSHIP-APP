package com.dicoding.membership.view.dashboard.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _userData = MutableLiveData<LoginDomain>()
    val userData: LiveData<LoginDomain> = _userData

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
        // Could be from SharedPreferences, DataStore, or other source
        return "current_user_id"
    }

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

}