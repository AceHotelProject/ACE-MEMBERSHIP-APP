package com.dicoding.core.domain.auth.repository

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.OtpDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {

    fun register(email: String, password: String): Flow<Resource<RegisterDomain>>

    fun login(email: String, password: String): Flow<Resource<LoginDomain>>

    fun getUser(): Flow<LoginDomain>

    suspend fun insertCacheUser(user: LoginDomain)

    fun saveAccessToken(token: String): Flow<Boolean>

    fun saveRefreshToken(token: String): Flow<Boolean>

    fun getAccessToken(): Flow<String>

    fun getRefreshToken(): Flow<String>

    suspend fun deleteToken()

    fun sendOtp(id: String): Flow<Resource<OtpDomain>>

    fun verifyOtp(id: String, token: Int): Flow<Resource<OtpDomain>>
}