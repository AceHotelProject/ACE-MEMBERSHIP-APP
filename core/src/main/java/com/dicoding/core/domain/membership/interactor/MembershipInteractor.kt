package com.dicoding.core.domain.membership.interactor

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.repository.IMembershipRepository
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MembershipInteractor @Inject constructor(
    private val membershipRepository: IMembershipRepository
) : MembershipUseCase {

    override fun createMembership(
        type: String,
        duration: Int,
        price: Int,
        tnc: List<String>
    ): Flow<Resource<Membership>> {
        return membershipRepository.createMembership(
            type = type,
            duration = duration,
            price = price,
            tnc = tnc
        )
    }

    override fun getAllMemberships(): Flow<Resource<List<Membership>>> {
        return membershipRepository.getAllMemberships()
    }

    override fun getMembershipById(id: String): Flow<Resource<Membership>> {
        return membershipRepository.getMembershipById(id)
    }

    override fun updateMembership(
        id: String,
        type: String?,
        duration: Int?,
        price: Int?,
        tnc: List<String>?
    ): Flow<Resource<Membership>> {
        return membershipRepository.updateMembership(
            id = id,
            type = type,
            duration = duration,
            price = price,
            tnc = tnc
        )
    }

    override fun deleteMembership(id: String): Flow<Resource<Unit>> {
        return membershipRepository.deleteMembership(id)
    }
}