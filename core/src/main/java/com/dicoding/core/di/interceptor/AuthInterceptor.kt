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

        Log.d("AuthInterceptor", "Access token: $token")

        val request = chain.request().newBuilder()
        if (token.isNotEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(request.build())
    }
}