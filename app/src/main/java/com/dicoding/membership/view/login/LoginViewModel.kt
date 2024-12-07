package com.dicoding.membership.view.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
//    private val authUseCaseTester: AuthUseCaseTester
    private val authUseCase: AuthUseCase
) : ViewModel() {

//    private val _loginResult = MutableStateFlow<Resource<LoginResponseDomain>>(Resource.Loading())
//    val loginResult: StateFlow<Resource<LoginResponseDomain>> = _loginResult
//
//    fun login(email: String, password: String) =
//        authUseCaseTester.loginUser(email, password).asLiveData()
//
//    private fun saveAccessToken(token: String) = authUseCaseTester.saveAccessToken(token).asLiveData()
//
//    fun getAccessToken() = authUseCaseTester.getAccessToken().asLiveData()
//
//    fun saveLoginStatus(isLogin: Boolean) = authUseCaseTester.saveLoginStatus(isLogin).asLiveData()
//
//    fun executeValidateToken(accessToken: String): MediatorLiveData<String> =
//        MediatorLiveData<String>().apply {
//            addSource(saveAccessToken(accessToken)) { accessTokenSaved ->
//                if (accessTokenSaved) {
//                    addSource(getAccessToken()) { token ->
//                        value = token
//                    }
//                }
//            }
//        }

    fun getUser() = authUseCase.getUser().asLiveData()

    fun login(email: String, password: String) =
        authUseCase.login(email, password).asLiveData()

    fun insertCacheUser(user: LoginDomain) = viewModelScope.launch {
        authUseCase.insertCacheUser(user)
    }

    private fun saveAccessToken(token: String) = authUseCase.saveAccessToken(token).asLiveData()

    private fun saveRefreshToken(token: String) = authUseCase.saveRefreshToken(token).asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun executeValidateToken(refreshToken: String, accessToken: String): MediatorLiveData<String> =
        MediatorLiveData<String>().apply {
            addSource(saveRefreshToken(refreshToken)) { refreshToken ->
                addSource(saveAccessToken(accessToken)) { accessToken ->

                    if (refreshToken && accessToken) {
                        addSource(getRefreshToken()) { token ->
                            value = token
                        }
                    }
                }
            }
        }
}
