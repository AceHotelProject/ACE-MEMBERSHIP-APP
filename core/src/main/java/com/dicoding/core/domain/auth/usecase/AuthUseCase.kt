package com.dicoding.core.domain.auth.usecase

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.OtpDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {

    fun register(email: String, password: String, androidId: String): Flow<Resource<RegisterDomain>>

    fun login(email: String, password: String, androidId: String): Flow<Resource<LoginDomain>>

    fun getUser(): Flow<LoginDomain>

    suspend fun deleteUser(user: LoginDomain)

    suspend fun insertCacheUser(user: LoginDomain)

    fun saveAccessToken(token: String): Flow<Boolean>

    fun saveRefreshToken(token: String): Flow<Boolean>

    fun saveMerchantId(id: String): Flow<Boolean>

    fun getAccessToken(): Flow<String>

    fun getRefreshToken(): Flow<String>

    fun getMerchantId(): Flow<String>

    suspend fun deleteAllData()

    fun saveEmailVerifiedStatus(isVerified: Boolean): Flow<Boolean>

    fun getEmailVerifiedStatus(): Flow<Boolean>

    fun sendOtp(): Flow<Resource<OtpDomain>>

    fun verifyOtp(token: String): Flow<Resource<Unit>>
}