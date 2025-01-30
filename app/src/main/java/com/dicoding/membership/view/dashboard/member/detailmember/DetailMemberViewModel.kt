package com.dicoding.membership.view.dashboard.member.detailmember

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailMemberViewModel @Inject constructor(
    private val userUseCase: UserUseCase
): ViewModel() {
    private val _userData = MutableLiveData<Resource<User>>()
    val userData: LiveData<Resource<User>> = _userData

    private val _deleteUserResult = MutableLiveData<Resource<Unit>>()
    val deleteUserResult: LiveData<Resource<Unit>> = _deleteUserResult

    fun getUserData(userId: String) {
        viewModelScope.launch {
            _userData.value = Resource.Loading()
            userUseCase.getUserData(userId)
                .catch { e ->
                    _userData.value = Resource.Error(e.message ?: "An error occurred")
                }
                .collect { result ->
                    _userData.value = result
                }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _deleteUserResult.value = Resource.Loading()
            userUseCase.deleteUser(userId)
                .catch { e ->
                    _deleteUserResult.value = Resource.Error(e.message ?: "Failed to delete user")
                }
                .collect { result ->
                    _deleteUserResult.value = result
                }
        }
    }
}