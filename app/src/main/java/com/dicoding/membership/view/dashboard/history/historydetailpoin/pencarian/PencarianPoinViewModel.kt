package com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.points.model.UserPointHistory
import com.dicoding.core.domain.points.usecase.PointsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PencarianPoinViewModel @Inject constructor(
    private val pointsUseCase: PointsUseCase,
    private val authUseCase: AuthUseCase
): ViewModel(){
    private val _userHistory = MutableStateFlow<Resource<UserPointHistory>>(Resource.Loading())
    val userHistory: StateFlow<Resource<UserPointHistory>> = _userHistory

    fun getUserHistory(userId: String) {
        viewModelScope.launch {
            pointsUseCase.getUserHistory(userId).collect {
                _userHistory.value = it
            }
        }
    }

    private val _userData = MutableLiveData<LoginDomain>()
    val userData: LiveData<LoginDomain> = _userData

    fun getUserData() {
        viewModelScope.launch {
            authUseCase.getUser()
                .collect { loginDomain ->
                    _userData.value = loginDomain
                }
        }
    }

}