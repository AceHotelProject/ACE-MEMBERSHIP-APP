package com.dicoding.membership.view.dashboard.profile.detail.poinku.terima

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerimaPoinViewModel @Inject constructor(
    private val userUseCase: UserUseCase
): ViewModel() {
    private val _userData = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userData = _userData.asStateFlow()

    fun getUserData(userId: String) {
        viewModelScope.launch {
            userUseCase.getUserData(userId)
                .collect { result ->
                    _userData.value = result
                }
        }
    }
}