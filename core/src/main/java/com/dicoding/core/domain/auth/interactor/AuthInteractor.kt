package com.dicoding.core.domain.auth.interactor

import com.dicoding.core.data.repository.AuthRepository
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.OtpDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthInteractor @Inject constructor(
    private val authRepository: AuthRepository
) : AuthUseCase {

    override fun register(email: String, password: String, androidId: String): Flow<Resource<RegisterDomain>> =
        authRepository.register(email, password, androidId)

    override fun login(email: String, password: String, androidId: String): Flow<Resource<LoginDomain>> =
        authRepository.login(email, password, androidId)

    override fun getUser(): Flow<LoginDomain> = authRepository.getUser()

    override suspend fun deleteUser(user: LoginDomain) = authRepository.deleteUser(user)

    override suspend fun insertCacheUser(user: LoginDomain) = authRepository.insertCacheUser(user)

    override fun saveAccessToken(token: String): Flow<Boolean> =
        authRepository.saveAccessToken(token)

    override fun saveRefreshToken(token: String): Flow<Boolean> =
        authRepository.saveRefreshToken(token)

    override fun saveMerchantId(id: String): Flow<Boolean> =
        authRepository.saveMerchantId(id)


    override fun getAccessToken(): Flow<String> = authRepository.getAccessToken()

    override fun getRefreshToken(): Flow<String> = authRepository.getRefreshToken()

    override fun getMerchantId(): Flow<String> = authRepository.getMerchantId()

    override suspend fun deleteAllData() = authRepository.deleteAllData()

    override fun saveEmailVerifiedStatus(isVerified: Boolean): Flow<Boolean> =
        authRepository.saveEmailVerifiedStatus(isVerified)

    override fun getEmailVerifiedStatus(): Flow<Boolean> =
        authRepository.getEmailVerifiedStatus()

    override fun sendOtp(): Flow<Resource<OtpDomain>> =
        authRepository.sendOtp()

    override fun verifyOtp(token: String): Flow<Resource<Unit>> =
        authRepository.verifyOtp(token)
}