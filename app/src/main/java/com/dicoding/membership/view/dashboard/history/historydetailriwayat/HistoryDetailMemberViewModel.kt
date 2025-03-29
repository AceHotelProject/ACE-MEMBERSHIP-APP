package com.dicoding.membership.view.dashboard.history.historydetailriwayat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class HistoryDetailMemberViewModel @Inject constructor(
    private val userUseCase: UserUseCase
): ViewModel() {
    private val _userData = MutableLiveData<Resource<User>>()
    val userData: LiveData<Resource<User>> = _userData

    private val _deleteUserResult = MutableLiveData<Resource<Unit>>()
    val deleteUserResult: LiveData<Resource<Unit>> = _deleteUserResult

    private val _verificatorData = MutableLiveData<Resource<User>>()
    val verificatorData: LiveData<Resource<User>> = _verificatorData

    fun getUserData(userId: String) {
        viewModelScope.launch {
            _userData.value = Resource.Loading()
            userUseCase.getUserData(userId)
                .catch { e ->
                    _userData.value = Resource.Error(e.message ?: "An error occurred")
                }
                .collect { result ->
                    _userData.value = result

                    // If user data is successfully retrieved, fetch verificator data if available
                    if (result is Resource.Success && result.data != null) {
                        // Safely access the verificatorId
                        val verificatorId = result.data!!.membership?.verificatorId
                        if (!verificatorId.isNullOrEmpty()) {
                            getVerificatorData(verificatorId)
                        }
                    }
                }
        }
    }

    fun getVerificatorData(verificatorId: String) {
        viewModelScope.launch {
            _verificatorData.value = Resource.Loading()
            userUseCase.getUserData(verificatorId)
                .catch { e ->
                    _verificatorData.value = Resource.Error(e.message ?: "Failed to fetch verificator data")
                }
                .collect { result ->
                    _verificatorData.value = result
                }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _deleteUserResult.value = Resource.Loading()
            try {
                withContext(Dispatchers.IO) {
                    userUseCase.deleteUser(userId)
                }
                // If we get here, it means the API call succeeded
                _deleteUserResult.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _deleteUserResult.value = Resource.Error(e.message ?: "Failed to delete user")
            }
        }
    }

    fun formatDate(isoDateString: String?): String {
        if (isoDateString.isNullOrEmpty()) return "-"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat = SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale("id"))
            outputFormat.timeZone = TimeZone.getDefault()

            val date = inputFormat.parse(isoDateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            "-"
        }
    }
}