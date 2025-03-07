package com.dicoding.core.domain.membership.interactor

import androidx.lifecycle.LiveData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.domain.membership.repository.IMembershipRepository
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MembershipInteractor @Inject constructor(
    private val membershipRepository: IMembershipRepository
) : MembershipUseCase {

    override fun createMembership(
        type: String,
        duration: Int,
        maxCoupon: Int,
        price: Int,
        tnc: List<String>,
        image: List<String>
    ): Flow<Resource<Membership>> {
        return membershipRepository.createMembership(
            type = type,
            duration = duration,
            maxCoupon = maxCoupon,
            price = price,
            tnc = tnc,
            image = image
        )
    }

    override fun getAllMemberships(): Flow<Resource<MembershipListResponse>> {
        return membershipRepository.getAllMemberships()
    }

    override fun getMembershipById(id: String): Flow<Resource<Membership>> {
        return membershipRepository.getMembershipById(id)
    }

    override fun updateMembership(
        id: String,
        type: String?,
        maxCoupon: Int?,
        duration: Int?,
        price: Int?,
        tnc: List<String>?,
        image: List<String>?
    ): Flow<Resource<Membership>> {
        return membershipRepository.updateMembership(
            id = id,
            type = type,
            maxCoupon = maxCoupon,
            duration = duration,
            price = price,
            tnc = tnc,
            image = image
        )
    }

    override fun deleteMembership(id: String): Flow<Resource<Unit>> {
        return membershipRepository.deleteMembership(id)
    }

    //LOCAL


    override fun getActiveMembership(): LiveData<MembershipLocal?> {
        return membershipRepository.getActiveMembership()
    }

    // Store membership data
    override suspend fun storeMembershipData(membership: Membership): Boolean {
        return membershipRepository.storeMembershipData(membership)
    }

    // Check if membership is expired
    override suspend fun checkMembershipExpiry(): Boolean {
        return membershipRepository.checkMembershipExpiry()
    }

    override suspend fun deleteLocalMembership(id: String): Boolean {
        return membershipRepository.deleteLocalMembership(id)
    }

    override suspend fun deleteAllLocalMemberships(): Boolean {
        return membershipRepository.deleteAllLocalMemberships()
    }

    override suspend fun updateRemainingCoupons(membershipId: String, remainingCoupons: Int): Boolean {
        return membershipRepository.updateRemainingCoupons(membershipId, remainingCoupons)
    }
}