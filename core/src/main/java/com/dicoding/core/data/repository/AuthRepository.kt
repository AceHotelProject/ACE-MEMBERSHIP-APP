package com.dicoding.core.data.repository



import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.local.LocalDataSource
import com.dicoding.core.data.source.local.datastore.DatastoreManager
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.OtpResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.model.OtpDomain
import com.dicoding.core.domain.auth.model.RegisterDomain
import com.dicoding.core.domain.auth.repository.IAuthRepository
import com.dicoding.core.utils.datamapper.AuthDataMapper
import com.dicoding.membership.core.utils.AppExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors,
    private val datastoreManager: DatastoreManager
) : IAuthRepository {

    override fun getUser(): Flow<LoginDomain> {
        return localDataSource.getUser().map {
            AuthDataMapper.mapUserEntityToDomain(it)
        }
    }

    override suspend fun insertCacheUser(user: LoginDomain) {
        val userEntity = AuthDataMapper.mapAuthToEntity(user)

        return appExecutors.diskIO().execute {
            GlobalScope.launch(Dispatchers.IO) {
                localDataSource.insertUser(userEntity)
            }
        }
    }

    override suspend fun deleteUser(user: LoginDomain) {
        val userEntity = AuthDataMapper.mapAuthToEntity(user)

        return appExecutors.diskIO().execute {
            GlobalScope.launch(Dispatchers.IO) {
                localDataSource.deleteUser(userEntity)
            }
        }
    }

    override fun register(email: String, password: String, androidId: String): Flow<Resource<RegisterDomain>> {
        return object : NetworkBoundResource<RegisterDomain, RegisterResponse>() {
            override suspend fun fetchFromApi(response: RegisterResponse): RegisterDomain {
                return AuthDataMapper.mapRegisterResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<RegisterResponse>> {
                return remoteDataSource.register(email, password, androidId)
            }
        }.asFlow()
    }

    override fun login(email: String, password: String, androidId: String): Flow<Resource<LoginDomain>> {
        return object : NetworkBoundResource<LoginDomain, LoginResponse>() {
            override suspend fun fetchFromApi(response: LoginResponse): LoginDomain {
                return AuthDataMapper.mapLoginResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<LoginResponse>> {
                return remoteDataSource.login(email, password, androidId)
            }
        }.asFlow()
    }

    override fun saveAccessToken(token: String): Flow<Boolean> {
        return datastoreManager.saveAccessToken(token)
    }

    override fun saveRefreshToken(token: String): Flow<Boolean> {
        return datastoreManager.saveRefreshToken(token)
    }

    override fun saveMerchantId(id: String): Flow<Boolean> {
        return datastoreManager.saveMerchantId(id)
    }

    override fun getAccessToken(): Flow<String> {
        return datastoreManager.getAccessToken()
    }

    override fun getRefreshToken(): Flow<String> {
        return datastoreManager.getRefreshToken()
    }

    override fun getMerchantId(): Flow<String> {
        return datastoreManager.getMerchantId()
    }

    override suspend fun deleteAllData() {
        return datastoreManager.deleteAllData()
    }

    override fun saveEmailVerifiedStatus(isVerified: Boolean): Flow<Boolean> =
        datastoreManager.saveEmailVerifiedStatus(isVerified)

    override fun getEmailVerifiedStatus(): Flow<Boolean> =
        datastoreManager.getEmailVerifiedStatus()

    override fun sendOtp(): Flow<Resource<OtpDomain>> {
        return object : NetworkBoundResource<OtpDomain, OtpResponse>() {
            override suspend fun fetchFromApi(response: OtpResponse): OtpDomain {
                return AuthDataMapper.mapOtpResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<OtpResponse>> {
                return remoteDataSource.sendOtp()
            }
        }.asFlow()
    }

    override fun verifyOtp(token: String): Flow<Resource<Unit>> {
        return object : NetworkBoundResource<Unit, Unit>() {
            override suspend fun fetchFromApi(response: Unit): Unit {
                return
            }

            override suspend fun createCall(): Flow<ApiResponse<Unit>> {
                return remoteDataSource.verifyOtp(token)
            }
        }.asFlow()
    }
}