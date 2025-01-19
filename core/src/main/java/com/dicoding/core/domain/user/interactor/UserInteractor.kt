package com.dicoding.core.domain.user.interactor

import com.dicoding.core.data.repository.UserRepository
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserInteractor @Inject constructor(private val userRepository: UserRepository) : UserUseCase {

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
        return userRepository.createUser(
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

    override fun getAllUsersData(): Flow<Resource<List<User>>> {
        return userRepository.getAllUsersData()
    }

    override fun getUserData(id: String): Flow<Resource<User>> =
        userRepository.getUserData(id)

    override fun updateUserData(
        id: String,
        idPicturePath: String?,
        name: String?,
        citizenNumber: String?,
        phone: String?,
        address: String?
    ): Flow<Resource<User>> {
        return userRepository.updateUserData(
            id = id,
            idPicturePath = idPicturePath,
            name = name,
            citizenNumber = citizenNumber,
            phone = phone,
            address = address
        )
    }

    override fun completeUserData(
        id: String,
        pathKTP: String?,
        citizenNumber: String?,
        phone: String?,
        address: String?
    ): Flow<Resource<User>> {
        return userRepository.completeUserData(
            id = id,
            pathKTP = pathKTP,
            citizenNumber = citizenNumber,
            phone = phone,
            address = address
        )
    }

    override fun getUserByPhone(phone: String): Flow<Resource<User>> {
        return userRepository.getUserByPhone(phone)
    }

    override fun deleteUser(id: String): Flow<Resource<Unit>> {
        return userRepository.deleteUser(id)
    }
}