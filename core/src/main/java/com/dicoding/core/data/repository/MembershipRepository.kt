package com.dicoding.core.data.repository

import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.local.LocalDataSource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.repository.IMembershipRepository
import com.dicoding.core.utils.datamapper.MembershipDataMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MembershipRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IMembershipRepository {

    override fun createMembership(
        type: String,
        duration: Int,
        price: Int,
        tnc: List<String>
    ): Flow<Resource<Membership>> {
        return object : NetworkBoundResource<Membership, MembershipResponse>() {
            override suspend fun fetchFromApi(response: MembershipResponse): Membership {
                return MembershipDataMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<MembershipResponse>> {
                return remoteDataSource.createMembership(
                    type = type,
                    duration = duration,
                    price = price,
                    tnc = tnc
                )
            }
        }.asFlow()
    }

    override fun getAllMemberships(): Flow<Resource<List<Membership>>> {
        return object : NetworkBoundResource<List<Membership>, List<MembershipResponse>>() {
            override suspend fun fetchFromApi(response: List<MembershipResponse>): List<Membership> {
                return response.map { MembershipDataMapper.mapResponseToDomain(it) }
            }

            override suspend fun createCall(): Flow<ApiResponse<List<MembershipResponse>>> {
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
        duration: Int?,
        price: Int?,
        tnc: List<String>?
    ): Flow<Resource<Membership>> {
        return object : NetworkBoundResource<Membership, MembershipResponse>() {
            override suspend fun fetchFromApi(response: MembershipResponse): Membership {
                return MembershipDataMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<MembershipResponse>> {
                return remoteDataSource.updateMembership(
                    id = id,
                    type = type,
                    duration = duration,
                    price = price,
                    tnc = tnc
                )
            }
        }.asFlow()
    }

    override fun deleteMembership(id: String): Flow<Resource<Unit>> {
        return object : NetworkBoundResource<Unit, Unit>() {
            override suspend fun fetchFromApi(response: Unit): Unit {
                return response
            }

            override suspend fun createCall(): Flow<ApiResponse<Unit>> {
                return remoteDataSource.deleteMembership(id)
            }
        }.asFlow()
    }

}