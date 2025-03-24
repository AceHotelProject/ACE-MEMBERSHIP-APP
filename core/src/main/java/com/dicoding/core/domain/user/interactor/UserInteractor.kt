package com.dicoding.core.domain.user.interactor

import com.dicoding.core.data.repository.UserRepository
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.ReferralToken
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.model.UserList
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

    override fun getAllUsersData(
        page: Int,
        search: String?,
        member: Boolean?,
        subscriptionType: String?,
        startDate: String?
    ): Flow<Resource<UserList>> {
        return userRepository.getAllUsersData(
            page = page,
            search = search,
            member = member,
            subscriptionType = subscriptionType,
            startDate = startDate
        )
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
        name: String?,
        pathKTP: String?,
        citizenNumber: String?,
        phone: String?,
        address: String?,
        subscriptionType: String?
    ): Flow<Resource<User>> {
        return userRepository.completeUserData(
            id = id,
            name = name,
            pathKTP = pathKTP,
            citizenNumber = citizenNumber,
            phone = phone,
            address = address,
            subscriptionType = subscriptionType
        )
    }

    override fun getUserByPhone(phone: String): Flow<Resource<User>> {
        return userRepository.getUserByPhone(phone)
    }

    override fun deleteUser(id: String): Flow<Resource<Unit>> {
        return userRepository.deleteUser(id)
    }

    override suspend fun createReferralToken(referralToken: String): Result<ReferralToken> {
        return userRepository.createReferralToken(referralToken)
    }

    override suspend fun getReferralToken(): Result<ReferralToken> {
        return userRepository.getReferralToken()
    }

    override fun verifyUser(
        id: String,
        paymentProof: String?
    ): Flow<Resource<User>> {
        return userRepository.verifyUser(
            id = id,
            paymentProof = paymentProof
        )
    }

    override fun subscribe(
        subscriptionType: String?
    ): Flow<Resource<User>> {
        return userRepository.subscribe(subscriptionType)
    }
}