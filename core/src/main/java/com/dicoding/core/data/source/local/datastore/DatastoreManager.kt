package com.dicoding.core.data.source.local.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatastoreManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    fun saveAccessToken(token: String): Flow<Boolean> {
        return flow {
            try {
                dataStore.edit { preferences ->
                    preferences[ACCESS_TOKEN_KEY] = token
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
                Log.e("DatastoreManager", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun saveRefreshToken(token: String): Flow<Boolean> {
        return flow {
            try {
                dataStore.edit { preferences ->
                    preferences[REFRESH_TOKEN_KEY] = token
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
                Log.e("DatastoreManager", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getAccessToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] ?: ""
        }
    }

    fun getRefreshToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY] ?: ""
        }
    }

    suspend fun deleteAllData() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(LOGIN_STATUS_KEY)
            preferences.remove(EMAIL_VERIFIED_KEY)
            preferences.remove(MERCHANT_ID_KEY)
        }
    }

    fun saveEmailVerifiedStatus(isVerified: Boolean): Flow<Boolean> {
        return flow {
            try {
                dataStore.edit { preferences ->
                    preferences[EMAIL_VERIFIED_KEY] = isVerified
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
                Log.e("DatastoreManager", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getEmailVerifiedStatus(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[EMAIL_VERIFIED_KEY] ?: false
        }
    }

    fun saveMerchantId(id: String): Flow<Boolean> {
        return flow {
            try {
                dataStore.edit { preferences ->
                    preferences[MERCHANT_ID_KEY] = id
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
                Log.e("DatastoreManager", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getMerchantId(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[MERCHANT_ID_KEY] ?: ""
        }
    }

    ///////////////////////////////////////////////////////////////////
    fun saveLoginStatus(isLogin: Boolean): Flow<Boolean> {
        return flow {
            try {
                dataStore.edit { preferences ->
                    preferences[LOGIN_STATUS_KEY] = isLogin
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
                Log.e("DatastoreManager", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getLoginStatus(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_STATUS_KEY] ?: false
        }
    }

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token_key")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token_key")
        private val EMAIL_VERIFIED_KEY = booleanPreferencesKey("email_verified_key")
        private val LOGIN_STATUS_KEY = booleanPreferencesKey("login_status_key")
        private val MERCHANT_ID_KEY = stringPreferencesKey("merchant_id_key")
    }
}
