package com.dicoding.membership.core.domain.auth.tester.repository

import com.dicoding.core.data.source.Resource
import com.dicoding.membership.core.domain.auth.tester.model.LoginResponseDomain
import com.dicoding.membership.core.domain.auth.tester.model.RegisterResponseDomain
import kotlinx.coroutines.flow.Flow

interface IAuthRepositoryTester {

    fun login(email: String, password: String): Flow<Resource<LoginResponseDomain>>

    fun register(name: String, email: String, password: String): Flow<Resource<RegisterResponseDomain>>

    //Token
    fun saveAccessToken(token: String): Flow<Boolean>

    fun getAccessToken(): Flow<String>

    suspend fun deleteToken()

    fun saveLoginStatus(isLogin: Boolean): Flow<Boolean>

    fun getLoginStatus(): Flow<Boolean>
}