package com.dicoding.core.data.repository

import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.local.LocalDataSource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.repository.IUserRepository
import com.dicoding.core.utils.datamapper.UserDataMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : IUserRepository {

    override fun createUser(
        email: String,
        password: String,
        name: String,
        role: String,
        memberType: String?,
        point: Int,
        subscriptionStartDate: String?,
        subscriptionEndDate: String?
    ): Flow<Resource<User>> {
        return object : NetworkBoundResource<User, UserResponse>() {
            override suspend fun fetchFromApi(response: UserResponse): User {
                return UserDataMapper.mapUserResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<UserResponse>> {
                return remoteDataSource.createUser(
                    email = email,
                    password = password,
                    name = name,
                    role = role,
                    memberType = memberType,
                    point = point,
                    subscriptionStartDate = subscriptionStartDate,
                    subscriptionEndDate = subscriptionEndDate
                )
            }
        }.asFlow()
    }

    override fun getAllUsersData(): Flow<Resource<List<User>>> {
        return object : NetworkBoundResource<List<User>, List<UserResponse>>() {
            override suspend fun fetchFromApi(response: List<UserResponse>): List<User> {
                return response.map { UserDataMapper.mapUserResponseToDomain(it) }
            }

            override suspend fun createCall(): Flow<ApiResponse<List<UserResponse>>> {
                return remoteDataSource.getAllUsersData()
            }
        }.asFlow()
    }

    override fun getUserData(id: String): Flow<Resource<User>> {
        return object : NetworkBoundResource<User, UserResponse>() {
            override suspend fun fetchFromApi(response: UserResponse): User {
                return UserDataMapper.mapUserResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<UserResponse>> {
                return remoteDataSource.getUserData(id)
            }
        }.asFlow()
    }

    override fun updateUserData(
        id: String,
        idPicturePath: String?,
        name: String?,
        citizenNumber: String?,
        phone: String?,
        address: String?
    ): Flow<Resource<User>> {
        return object : NetworkBoundResource<User, UserResponse>() {
            override suspend fun fetchFromApi(response: UserResponse): User {
                return UserDataMapper.mapUserResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<UserResponse>> {
                return remoteDataSource.updateUserData(
                    id = id,
                    idPicturePath = idPicturePath,
                    name = name,
                    citizenNumber = citizenNumber,
                    phone = phone,
                    address = address
                )
            }
        }.asFlow()
    }

    override fun completeUserData(
        id: String,
        name: String?,
        pathKTP: String?,
        citizenNumber: String?,
        phone: String?,
        address: String?
    ): Flow<Resource<User>> {
        return object : NetworkBoundResource<User, UserResponse>() {
            override suspend fun fetchFromApi(response: UserResponse): User {
                return UserDataMapper.mapUserResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<UserResponse>> {
                return remoteDataSource.completeUserData(
                    id = id,
                    name = name,
                    pathKTP = pathKTP,
                    citizenNumber = citizenNumber,
                    phone = phone,
                    address = address
                )
            }
        }.asFlow()
    }

    override fun getUserByPhone(phone: String): Flow<Resource<User>> {
        return object : NetworkBoundResource<User, UserResponse>() {
            override suspend fun fetchFromApi(response: UserResponse): User {
                return UserDataMapper.mapUserResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<UserResponse>> {
                return remoteDataSource.getUserByPhone(phone)
            }
        }.asFlow()
    }

    override fun deleteUser(id: String): Flow<Resource<Unit>> {
        return object : NetworkBoundResource<Unit, Unit>() {
            override suspend fun fetchFromApi(response: Unit): Unit {
                return response
            }

            override suspend fun createCall(): Flow<ApiResponse<Unit>> {
                return remoteDataSource.deleteUser(id)
            }
        }.asFlow()
    }
}