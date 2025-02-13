package com.dicoding.membership.view.dashboard.profile.detail.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor (
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


}