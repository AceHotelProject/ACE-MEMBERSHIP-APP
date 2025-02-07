package com.dicoding.core.domain.user.repository

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.model.UserList
import kotlinx.coroutines.flow.Flow

interface IUserRepository {

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

    fun getAllUsersData(page: Int): Flow<Resource<UserList>>

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
        pathKTP: String? = null,
        citizenNumber: String? = null,
        phone: String? = null,
        address: String? = null
    ): Flow<Resource<User>>

    fun getUserByPhone(phone: String): Flow<Resource<User>>

    fun deleteUser(id: String): Flow<Resource<Unit>>
}