package com.dicoding.core.data.repository

import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.local.LocalDataSource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponse
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.domain.points.repository.IPointsRepository
import com.dicoding.core.utils.datamapper.PointsDataMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointsRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IPointsRepository {
    override fun getUserPoints(userId: String): Flow<Resource<Points>> {
        return object : NetworkBoundResource<Points, PointsResponse>() {
            override suspend fun fetchFromApi(response: PointsResponse): Points {
                return PointsDataMapper.mapResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<PointsResponse>> {
                return remoteDataSource.getUserPoints(userId)
            }
        }.asFlow()
    }

    override fun getUserPointHistory(userId: String): Flow<Resource<List<PointHistory>>> {
        return object : NetworkBoundResource<List<PointHistory>, List<PointHistoryResponse>>() {
            override suspend fun fetchFromApi(response: List<PointHistoryResponse>): List<PointHistory> {
                return response.map { PointsDataMapper.mapHistoryResponseToDomain(it) }
            }

            override suspend fun createCall(): Flow<ApiResponse<List<PointHistoryResponse>>> {
                return remoteDataSource.getUserPointHistory(userId)
            }
        }.asFlow()
    }

    override fun transferPoints(
        userId: String,
        userReceiverId: String,
        amount: Int,
        note: String,
        purpose: String
    ): Flow<Resource<Unit>> {
        return object : NetworkBoundResource<Unit, Unit>() {
            override suspend fun fetchFromApi(response: Unit): Unit {
                return response
            }

            override suspend fun createCall(): Flow<ApiResponse<Unit>> {
                return remoteDataSource.transferPoints(
                    userId = userId,
                    userReceiverId = userReceiverId,
                    amount = amount,
                    note = note,
                    purpose = purpose
                )
            }
        }.asFlow()
    }
}