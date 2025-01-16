package com.dicoding.core.domain.membership.usecase

import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.membership.ValidatedMembership
import com.dicoding.core.domain.membership.model.Membership
import kotlinx.coroutines.flow.Flow

interface MembershipUseCase {
    fun createMembership(
        name: String,
        periode: Int,
        price: Int,
        tnc: List<String>,
        discount: Int
    ): Flow<Resource<Membership>>

    fun getAllMemberships(): Flow<Resource<List<Membership>>>

    fun getMembershipById(id: String): Flow<Resource<Membership>>

    fun updateMembership(
        id: String,
        name: String? = null,
        periode: Int? = null,
        price: Int? = null,
        tnc: List<String>? = null,
        discount: Int? = null
    ): Flow<Resource<Membership>>

    fun deleteMembership(id: String): Flow<Resource<Unit>>

    fun validateMembership(
        userId: String,
        type: String,
        price: Int,
        startDate: String,
        endDate: String,
        status: String,
        proofImagePath: String,
        verifiedBy: String
    ): Flow<Resource<ValidatedMembership>>
}