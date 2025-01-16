package com.dicoding.membership.view.dashboard.profile.detail.detail.ubahprofil

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
class UbahProfileViewModel @Inject constructor (
    private val userUseCase: UserUseCase
): ViewModel() {
    fun updateUserData(
        id: String,
        idPicturePath: String? = "dummy_image_path.jpg",
        name: String? = "Default Name",
        citizenNumber: String? = "0000000000000000",
        phone: String? = "000000000000",
        address: String? = "no address"
    ) = userUseCase.updateUserData(
        id = id,
        idPicturePath = idPicturePath,
        name = name,
        citizenNumber = citizenNumber,
        phone = phone,
        address = address
    ).asLiveData()


}