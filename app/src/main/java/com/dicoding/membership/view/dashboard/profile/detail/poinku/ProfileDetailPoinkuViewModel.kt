package com.dicoding.membership.view.dashboard.profile.detail.poinku

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.model.UserPointHistory
import com.dicoding.core.domain.points.usecase.PointsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailPoinkuViewModel @Inject constructor (
    private val pointsUseCase: PointsUseCase
): ViewModel() {
    private val _points = MutableStateFlow<Resource<Points>>(Resource.Loading())
    val points = _points.asStateFlow()

    private val _userHistory = MutableStateFlow<Resource<UserPointHistory>>(Resource.Loading())
    val userHistory: StateFlow<Resource<UserPointHistory>> = _userHistory

    fun getUserPoints(userId: String) {
        Log.d("Points", "Fetching points for user: $userId")
        viewModelScope.launch {
            pointsUseCase.getUserPoints(userId)
                .collect { result ->
                    Log.d("Points", "Received points result: $result")
                    _points.value = result
                }
        }
    }

    fun getUserHistory(userId: String) {
        viewModelScope.launch {
            pointsUseCase.getUserHistory(userId).collect {
                _userHistory.value = it
            }
        }
    }
}