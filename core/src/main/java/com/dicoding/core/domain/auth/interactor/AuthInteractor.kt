package com.dicoding.core.domain.auth.interactor

import com.dicoding.core.data.repository.AuthRepository
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.OtpDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.model.UserDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthInteractor @Inject constructor(
    private val authRepository: AuthRepository
) : AuthUseCase {

    override fun register(email: String, password: String): Flow<Resource<RegisterDomain>> =
        authRepository.register(email, password)

    override fun login(email: String, password: String): Flow<Resource<LoginDomain>> =
        authRepository.login(email, password)

    override fun getUser(): Flow<LoginDomain> = authRepository.getUser()

    override suspend fun insertCacheUser(user: LoginDomain) = authRepository.insertCacheUser(user)

    override fun saveAccessToken(token: String): Flow<Boolean> =
        authRepository.saveAccessToken(token)

    override fun saveRefreshToken(token: String): Flow<Boolean> =
        authRepository.saveRefreshToken(token)

    override fun getAccessToken(): Flow<String> = authRepository.getAccessToken()

    override fun getRefreshToken(): Flow<String> = authRepository.getRefreshToken()

    override suspend fun deleteToken() = authRepository.deleteToken()

    override fun sendOtp(id: String): Flow<Resource<OtpDomain>> {
        return authRepository.sendOtp(id)
    }

    override fun verifyOtp(id: String, token: Int): Flow<Resource<OtpDomain>> {
        return authRepository.verifyOtp(id, token)
    }
}