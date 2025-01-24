package com.dicoding.membership.view.dashboard.home.member.mregister

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeMemberRegisterViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase
): ViewModel() {
    var imagePath: String? = null

    fun setImageString(path: String) {
        imagePath = path
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

    fun completeUserData(
        id: String,
        name: String? = null,
        pathKTP: String? = null,
        citizenNumber: String? = null,
        phone: String? = null,
        address: String? = null,
        memberType: String? = null
    ) = userUseCase.completeUserData(
        id = id,
        name = name,
        pathKTP = pathKTP,
        citizenNumber = citizenNumber,
        phone = phone,
        address = address,
        memberType = memberType
    ).asLiveData()
}