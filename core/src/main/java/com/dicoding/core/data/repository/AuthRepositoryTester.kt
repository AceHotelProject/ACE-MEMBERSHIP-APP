package com.dicoding.core.data.repository

import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.local.datastore.DatastoreManager
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.test.LoginTest
import com.dicoding.core.data.source.remote.response.test.RegisterTest
import com.dicoding.membership.core.domain.auth.tester.model.LoginResponseDomain
import com.dicoding.membership.core.domain.auth.tester.model.RegisterResponseDomain
import com.dicoding.membership.core.domain.auth.tester.repository.IAuthRepositoryTester
import com.dicoding.membership.core.utils.datamapper.AuthDataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryTester @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val datastoreManager: DatastoreManager
) : IAuthRepositoryTester {

    override fun login(email: String, password: String): Flow<Resource<LoginResponseDomain>> {
        return object : NetworkBoundResource<LoginResponseDomain, LoginTest>() {
            override suspend fun fetchFromApi(response: LoginTest): LoginResponseDomain {
                return AuthDataMapper.mapLoginTestToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<LoginTest>> {
                return remoteDataSource.loginTester(email, password)
            }
        }.asFlow()
    }

    override fun register(name: String, email: String, password: String): Flow<Resource<RegisterResponseDomain>> {
        return object : NetworkBoundResource<RegisterResponseDomain, RegisterTest>() {
            override suspend fun fetchFromApi(response: RegisterTest): RegisterResponseDomain {
                return AuthDataMapper.mapRegisterTestToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<RegisterTest>> {
                return remoteDataSource.registerTester(name, email, password)
            }
        }.asFlow()
    }

    override fun saveAccessToken(token: String): Flow<Boolean> {
        return datastoreManager.saveAccessToken(token)
    }

    override fun getAccessToken(): Flow<String> {
        return datastoreManager.getAccessToken()
    }

    override suspend fun deleteToken() {
        return datastoreManager.deleteToken()
    }

    override fun saveLoginStatus(isLogin: Boolean): Flow<Boolean> {
        return datastoreManager.saveLoginStatus(isLogin)
    }

    override fun getLoginStatus(): Flow<Boolean> {
        return datastoreManager.getLoginStatus().flowOn(Dispatchers.IO)
    }
}