package com.dicoding.core.domain.membership.usecase

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.membership.model.Membership
import kotlinx.coroutines.flow.Flow

interface MembershipUseCase {
    fun createMembership(
        type: String,
        duration: Int,
        price: Int,
        tnc: List<String>
    ): Flow<Resource<Membership>>

    fun getAllMemberships(): Flow<Resource<List<Membership>>>

    fun getMembershipById(id: String): Flow<Resource<Membership>>

    fun updateMembership(
        id: String,
        type: String? = null,
        duration: Int? = null,
        price: Int? = null,
        tnc: List<String>? = null
    ): Flow<Resource<Membership>>

    fun deleteMembership(id: String): Flow<Resource<Unit>>

}