package com.dicoding.core.di.interceptor

import android.util.Log
import com.dicoding.core.data.source.local.datastore.DatastoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val datastoreManager: DatastoreManager) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            datastoreManager.getAccessToken().first().toString()
        }

        //debugging purposes
        Log.d("AuthInterceptor", "────────────────────────────")
        Log.d("AuthInterceptor", "Starting network request")
        Log.d("AuthInterceptor", "URL: ${chain.request().url}")
        Log.d("AuthInterceptor", "Method: ${chain.request().method}")
        Log.d("AuthInterceptor", "Headers: ${chain.request().headers}")
        Log.d("AuthInterceptor", "Access token: $token")

        val request = chain.request().newBuilder()
        if (token.isNotEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(request.build())

        Log.d("AuthInterceptor", "Response code: ${response.code}")
        Log.d("AuthInterceptor", "Response message: ${response.message}")
        Log.d("AuthInterceptor", "────────────────────────────")

        return response

    }
}