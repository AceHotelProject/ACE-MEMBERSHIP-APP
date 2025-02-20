package com.dicoding.membership.view.dashboard.profile.detail.poinku

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.model.UserPointHistory
import com.dicoding.core.domain.points.usecase.PointsUseCase
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailPoinkuViewModel @Inject constructor (
    private val pointsUseCase: PointsUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {
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

    private val _points = MutableStateFlow<Resource<Points>>(Resource.Loading())
    val points = _points.asStateFlow()

    private val _userHistory = MutableStateFlow<Resource<UserPointHistory>>(Resource.Loading())
    val userHistory: StateFlow<Resource<UserPointHistory>> = _userHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    fun getUserPoints(userId: String) {
        Log.d("Points", "Fetching points for user: $userId")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                pointsUseCase.getUserPoints(userId)
                    .collect { result ->
                        Log.d("Points", "Received points result: $result")
                        _points.value = result
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserHistory(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                pointsUseCase.getUserHistory(userId).collect {
                    _userHistory.value = it
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData(userId: String) {
        getUserPoints(userId)
        getUserHistory(userId)
    }
}