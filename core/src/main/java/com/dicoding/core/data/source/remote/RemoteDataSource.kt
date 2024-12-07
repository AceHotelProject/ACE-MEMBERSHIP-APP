package com.dicoding.core.data.source.remote

import android.content.ContentValues.TAG
import android.util.Log
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.network.ApiService
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.test.DetailStoryResponse
import com.dicoding.core.data.source.remote.response.test.LoginTest
import com.dicoding.core.data.source.remote.response.test.RegisterTest
import com.dicoding.core.data.source.remote.response.test.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    // Test
    suspend fun loginTester(email: String, password: String): Flow<ApiResponse<LoginTest>> {
        return flow {
            try {
                Log.d(TAG, "Login request: email=$email, password=$password")
                val response = apiService.loginTest(email, password)

                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Login error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun registerTester(name: String, email: String, password: String): Flow<ApiResponse<RegisterTest>> {
        return flow {
            try {
                Log.d(TAG, "Register request: name=$name, email=$email, password=$password")
                val response = apiService.registerTest(name, email, password)

                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Register error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }
    // Test

    suspend fun getStories(page: Int, size: Int): StoryResponse {
        return apiService.getStories(page, size)
    }

    suspend fun getDetailStories(id: String): Flow<ApiResponse<DetailStoryResponse>> {
        return flow {
            try {
                val response = apiService.getDetailStories(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    ///////////////////////////////////////////////////////////////////////////////

    suspend fun login(email: String, password: String): Flow<ApiResponse<LoginResponse>> {
        return flow {
            try {
                Log.d(TAG, "Login attempt for email: $email")
                val response = apiService.login(email, password)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                Log.e(TAG, "Login failed: ${e.message}", e)
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun register(email: String, password: String): Flow<ApiResponse<RegisterResponse>> {
        return flow {
            try {
                Log.d(TAG, "Register attempt for email: $email")
                val response = apiService.register(email, password)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                Log.e(TAG, "Register failed: ${e.message}", e)
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }
}
