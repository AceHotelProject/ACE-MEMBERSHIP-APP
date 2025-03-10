package com.dicoding.core.domain.membership.usecase

import androidx.lifecycle.LiveData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.domain.membership.model.SubscriptionHistory
import com.dicoding.core.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface MembershipUseCase {
    fun createMembership(
        type: String,
        duration: Int,
        maxCoupon: Int,
        price: Int,
        tnc: List<String>,
        image: List<String>
    ): Flow<Resource<Membership>>

    fun getAllMemberships(): Flow<Resource<MembershipListResponse>>

    fun getMembershipById(id: String): Flow<Resource<Membership>>

    fun updateMembership(
        id: String,
        type: String? = null,
        maxCoupon: Int? = null,
        duration: Int? = null,
        price: Int? = null,
        tnc: List<String>? = null,
        image: List<String>? = null
    ): Flow<Resource<Membership>>

    fun deleteMembership(id: String): Flow<Resource<Unit>>

    //LOCAL

    fun getActiveMembership(): LiveData<MembershipLocal?>

    // Store membership data
    suspend fun storeMembershipData(membership: Membership): Boolean

    // Check if membership is expired
    suspend fun checkMembershipExpiry(): Boolean

    suspend fun deleteLocalMembership(id: String): Boolean

    suspend fun deleteAllLocalMemberships(): Boolean

    suspend fun updateRemainingCoupons(membershipId: String, remainingCoupons: Int): Boolean

    fun getSubscriptionHistory(
        page: Int? = null,
        limit: Int? = null,
        search: String? = null,
        time: String? = null,
        type: String? = null
    ): Flow<Resource<SubscriptionHistory>>
}