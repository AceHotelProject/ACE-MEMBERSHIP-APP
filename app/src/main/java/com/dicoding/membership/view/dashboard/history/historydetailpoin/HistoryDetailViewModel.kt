package com.dicoding.membership.view.dashboard.history.poin.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {
    private val _userData = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userData: StateFlow<Resource<User>> = _userData

    fun getOtherUserData(history: PointHistory, isReceiving: Boolean) {
        val otherUserId = if (isReceiving) history.from.id else history.to.id
        viewModelScope.launch {
            userUseCase.getUserData(otherUserId).collect {
                _userData.value = it
            }
        }
    }
}