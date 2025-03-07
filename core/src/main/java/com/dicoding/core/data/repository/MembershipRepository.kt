package com.dicoding.core.data.repository

import androidx.lifecycle.LiveData
import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.local.LocalDataSource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.domain.membership.repository.IMembershipRepository
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.utils.datamapper.MembershipDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MembershipRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : IMembershipRepository {

    override fun createMembership(
        type: String,
        duration: Int,
        maxCoupon: Int,
        price: Int,
        tnc: List<String>,
        image: List<String>
    ): Flow<Resource<Membership>> {
        return object : NetworkBoundResource<Membership, MembershipResponse>() {
            override suspend fun fetchFromApi(response: MembershipResponse): Membership {
                return MembershipDataMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<MembershipResponse>> {
                return remoteDataSource.createMembership(
                    type = type,
                    duration = duration,
                    maxCoupon = maxCoupon,
                    price = price,
                    tnc = tnc,
                    image = image
                )
            }
        }.asFlow()
    }

    override fun getAllMemberships(): Flow<Resource<MembershipListResponse>> {
        return object : NetworkBoundResource<MembershipListResponse, MembershipListResponse>() {
            override suspend fun fetchFromApi(response: MembershipListResponse): MembershipListResponse {
                return MembershipListResponse(
                    results = response.results.map {
                        MembershipResponse(
                            id = it.id,
                            type = it.type,
                            duration = it.duration,
                            price = it.price,
                            tnc = it.tnc
                        )
                    },
                    page = response.page,
                    limit = response.limit,
                    totalPages = response.totalPages,
                    totalResults = response.totalResults
                )
            }

            override suspend fun createCall(): Flow<ApiResponse<MembershipListResponse>> {
                return remoteDataSource.getAllMemberships()
            }
        }.asFlow()
    }

    override fun getMembershipById(id: String): Flow<Resource<Membership>> {
        return object : NetworkBoundResource<Membership, MembershipResponse>() {
            override suspend fun fetchFromApi(response: MembershipResponse): Membership {
                return MembershipDataMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<MembershipResponse>> {
                return remoteDataSource.getMembershipById(id)
            }
        }.asFlow()
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
        return object : NetworkBoundResource<Membership, MembershipResponse>() {
            override suspend fun fetchFromApi(response: MembershipResponse): Membership {
                return MembershipDataMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<MembershipResponse>> {
                return remoteDataSource.updateMembership(
                    id = id,
                    type = type,
                    maxCoupon = maxCoupon,
                    duration = duration,
                    price = price,
                    tnc = tnc,
                    image = image
                )
            }
        }.asFlow()
    }

    override fun deleteMembership(id: String): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.deleteMembership(id).collect { apiResponse ->
                when (apiResponse) {
                    is ApiResponse.Success -> {
                        // For 204 No Content, we just emit Success with Unit
                        emit(Resource.Success(Unit))
                    }
                    is ApiResponse.Error -> {
                        emit(Resource.Error(apiResponse.errorMessage))
                    }
                    is ApiResponse.Empty -> {
                        // In case of 204, this might also be considered a success
                        emit(Resource.Success(Unit))
                    }
                }
            }
        }
    }

    //////////////////// LOCAL DB
    // 1. Get active membership from local database
    override fun getActiveMembership(): LiveData<MembershipLocal?> {
        return localDataSource.getActiveMembership()
    }

    // 2. Store membership data to local database (from User object)
    override suspend fun storeMembershipData(membership: Membership): Boolean {
        return try {
            val membershipLocal = convertToLocalModel(membership)
            localDataSource.saveMembership(membershipLocal)
            true
        } catch (e: Exception) {
            Timber.e(e, "Error storing membership data")
            false
        }
    }

    // 3. Check if membership is expired
    override suspend fun checkMembershipExpiry(): Boolean {
        return localDataSource.checkMembershipExpiry()
    }

    override suspend fun updateRemainingCoupons(membershipId: String, remainingCoupons: Int): Boolean {
        return try {
            localDataSource.updateRemainingCoupons(membershipId, remainingCoupons)
            true
        } catch (e: Exception) {
            Timber.e(e, "Error updating remaining coupons")
            false
        }
    }

    // Helper method to convert User model to MembershipLocal
    private fun convertToLocalModel(membership: Membership): MembershipLocal {
        // Calculate purchase date (current date) and expiry date based on duration
        val purchaseDate = Date()
        val expiryDate = Calendar.getInstance().apply {
            time = purchaseDate
            add(Calendar.DAY_OF_YEAR, membership.duration)
        }.time

        return MembershipLocal(
            id = membership.id,
            userId = null, // This might need to be populated from elsewhere if needed
            type = membership.type,
            duration = membership.duration,
            price = membership.price.toLong(),
            image = membership.image,
            maxCoupon = membership.maxCoupon,
            tnc = membership.tnc,
            createdAt = membership.createdAt,
            purchaseDate = purchaseDate,
            expiryDate = expiryDate,
            isActive = true, // Assume active by default
            remainingCoupons = membership.maxCoupon, // Start with max coupons
            lastUpdatedAt = Date() // Current time
        )
    }

    override suspend fun deleteLocalMembership(id: String): Boolean {
        return try {
            localDataSource.deleteMembership(id)
            true
        } catch (e: Exception) {
            Timber.e(e, "Error deleting local membership data")
            false
        }
    }

    override suspend fun deleteAllLocalMemberships(): Boolean {
        return try {
            localDataSource.deleteAllMemberships()
            true
        } catch (e: Exception) {
            Timber.e(e, "Error deleting all local membership data")
            false
        }
    }


}