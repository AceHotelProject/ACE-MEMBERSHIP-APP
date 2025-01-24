package com.dicoding.membership.view.dashboard.profile.detail.poinku.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.usecase.PointsUseCase
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferPoinViewModel @Inject constructor (
    private val userUseCase: UserUseCase,
    private val pointsUseCase: PointsUseCase
): ViewModel() {
    private val _userData = MutableLiveData<Resource<User>>()
    val userData: LiveData<Resource<User>> = _userData

    fun getUserDataByPhone(phone: String) {
        viewModelScope.launch {
            // Emit loading state
            _userData.value = Resource.Loading()

            userUseCase.getUserByPhone(phone)
                .catch { e ->
                    _userData.value = Resource.Error(e.message ?: "Nah")
                }
                .collect { result ->
                    _userData.value = result
                }
        }
    }

    private val _transferResult = MutableLiveData<Resource<PointHistory>>()
    val transferResult: LiveData<Resource<PointHistory>> = _transferResult

    fun submitTransfer(to: String, from: String, amount: Int, notes: String) {
        viewModelScope.launch {
            pointsUseCase.transferPoints(to, from, amount, notes)
                .onStart { _transferResult.value = Resource.Loading() }
                .catch { e -> _transferResult.value = Resource.Error(e.message ?: "Transfer failed") }
                .collect { result -> _transferResult.value = result }
        }
    }


}