package com.dicoding.core.data.repository

import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.local.LocalDataSource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.repository.IMembershipRepository
import com.dicoding.core.utils.datamapper.MembershipDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

}