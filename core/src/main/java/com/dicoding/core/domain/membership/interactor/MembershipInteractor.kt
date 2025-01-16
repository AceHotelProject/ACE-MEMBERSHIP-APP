package com.dicoding.core.domain.membership.interactor

import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.membership.ValidatedMembership
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.repository.IMembershipRepository
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MembershipInteractor @Inject constructor(
    private val membershipRepository: IMembershipRepository
) : MembershipUseCase {

    override fun createMembership(
        name: String,
        periode: Int,
        price: Int,
        tnc: List<String>,
        discount: Int
    ): Flow<Resource<Membership>> {
        return membershipRepository.createMembership(
            name = name,
            periode = periode,
            price = price,
            tnc = tnc,
            discount = discount
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
        name: String?,
        periode: Int?,
        price: Int?,
        tnc: List<String>?,
        discount: Int?
    ): Flow<Resource<Membership>> {
        return membershipRepository.updateMembership(
            id = id,
            name = name,
            periode = periode,
            price = price,
            tnc = tnc,
            discount = discount
        )
    }

    override fun deleteMembership(id: String): Flow<Resource<Unit>> {
        return membershipRepository.deleteMembership(id)
    }

    override fun validateMembership(
        userId: String,
        type: String,
        price: Int,
        startDate: String,
        endDate: String,
        status: String,
        proofImagePath: String,
        verifiedBy: String
    ): Flow<Resource<ValidatedMembership>> {
        return membershipRepository.validateMembership(
            userId = userId,
            type = type,
            price = price,
            startDate = startDate,
            endDate = endDate,
            status = status,
            proofImagePath = proofImagePath,
            verifiedBy = verifiedBy
        )
    }
}