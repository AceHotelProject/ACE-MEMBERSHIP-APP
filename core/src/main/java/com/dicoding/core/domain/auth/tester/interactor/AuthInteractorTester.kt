package com.dicoding.membership.core.domain.auth.tester.interactor

import com.dicoding.core.data.repository.AuthRepositoryTester
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.tester.usecase.AuthUseCaseTester
import com.dicoding.membership.core.domain.auth.tester.model.LoginResponseDomain
import com.dicoding.membership.core.domain.auth.tester.model.RegisterResponseDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthInteractorTester @Inject constructor(private val authRepositoryTester: AuthRepositoryTester)
    : AuthUseCaseTester {

    override fun loginUser(email: String, password: String): Flow<Resource<LoginResponseDomain>> {
        return authRepositoryTester.login(email, password)
    }

    override fun registerUser(name: String, email: String, password: String): Flow<Resource<RegisterResponseDomain>> {
        return authRepositoryTester.register(name, email, password)
    }

    override fun saveAccessToken(token: String): Flow<Boolean> {
        return authRepositoryTester.saveAccessToken(token)
    }

    override fun getAccessToken(): Flow<String> {
        return authRepositoryTester.getAccessToken()
    }

    override suspend fun deleteToken() {
        authRepositoryTester.deleteToken()
    }

    override fun saveLoginStatus(isLogin: Boolean): Flow<Boolean> {
        return authRepositoryTester.saveLoginStatus(isLogin)
    }

    override fun getLoginStatus(): Flow<Boolean> {
        return authRepositoryTester.getLoginStatus()
    }
}