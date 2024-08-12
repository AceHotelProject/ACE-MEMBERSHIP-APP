package com.dicoding.membership.core.di.interceptor

import android.util.Log
import com.dicoding.core.data.source.local.datastore.DatastoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val datastoreManager: DatastoreManager,
) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = runBlocking {
            datastoreManager.getAccessToken().first().toString()
        }

        return if (response.code == 401 && accessToken.isNotEmpty()) {
            runBlocking {
                datastoreManager.deleteToken()
                datastoreManager.saveLoginStatus(false).first()

                Log.e("AuthAuthenticator", "Token expired. User logged out.")
                null
            }
        } else {
            null
        }
    }
}