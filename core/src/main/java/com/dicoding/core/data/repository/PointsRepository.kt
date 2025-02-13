package com.dicoding.core.data.repository

import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.model.UserPointHistory
import com.dicoding.core.domain.points.repository.IPointsRepository
import com.dicoding.core.utils.datamapper.PointsDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointsRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IPointsRepository {
    override fun getUserPoints(userId: String): Flow<Resource<Points>> {
        return object : NetworkBoundResource<Points, PointsResponse>() {
            override suspend fun fetchFromApi(response: PointsResponse): Points {
                return PointsDataMapper.mapPointsResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<PointsResponse>> {
                return remoteDataSource.getUserPoints(userId)
            }
        }.asFlow()
    }

    override fun transferPoints(
        to: String,
        from: String,
        amount: Int,
        notes: String
    ): Flow<Resource<PointHistory>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.transferPoints(to, from, amount, notes).first()) {
                is ApiResponse.Success -> {
                    val domainData = PointsDataMapper.mapPointTransferResponseToDomain(apiResponse.data)
                    emit(Resource.Success(domainData))
                }
                is ApiResponse.Empty -> {
                    emit(Resource.Error("Empty response"))
                }
                is ApiResponse.Error -> {
                    emit(Resource.Error(apiResponse.errorMessage))
                }
            }
        }
    }

    override fun getUserHistory(userId: String): Flow<Resource<UserPointHistory>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUserHistory(userId).first()) {
                is ApiResponse.Success -> {
                    val domainData = PointsDataMapper.mapUserPointsHistoryResponseToDomain(apiResponse.data)
                    emit(Resource.Success(domainData))
                }
                is ApiResponse.Empty -> {
                    emit(Resource.Error("Empty response"))
                }
                is ApiResponse.Error -> {
                    emit(Resource.Error(apiResponse.errorMessage))
                }
            }
        }
    }
}