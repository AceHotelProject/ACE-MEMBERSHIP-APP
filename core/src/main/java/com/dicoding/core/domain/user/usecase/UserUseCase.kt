package com.dicoding.core.domain.user.usecase

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserUseCase {

    fun createUser(
        email: String,
        password: String,
        name: String,
        role: String = "member",
        memberType: String? = null,
        point: Int = 0,
        subscriptionStartDate: String? = null,
        subscriptionEndDate: String? = null
    ): Flow<Resource<User>>

    fun getAllUsersData(): Flow<Resource<List<User>>>

    fun getUserData(id: String): Flow<Resource<User>>

    fun updateUserData(
        id: String,
        idPicturePath: String? = null,
        name: String? = null,
        citizenNumber: String? = null,
        phone: String? = null,
        address: String? = null
    ): Flow<Resource<User>>

    fun completeUserData(
        id: String,
        name: String? = null,
        pathKTP: String? = null,
        citizenNumber: String? = null,
        phone: String? = null,
        address: String? = null
    ): Flow<Resource<User>>

    fun getUserByPhone(phone: String): Flow<Resource<User>>

    fun deleteUser(id: String): Flow<Resource<Unit>>
}