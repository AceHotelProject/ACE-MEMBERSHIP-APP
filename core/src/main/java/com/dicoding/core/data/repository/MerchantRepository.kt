package com.dicoding.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.paging.MerchantsPagingSource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantRequest
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantResponse
import com.dicoding.core.data.source.remote.response.merchants.GetMerchantsByIdResponse
import com.dicoding.core.data.source.remote.response.merchants.UpdateMerchantResponse
import com.dicoding.core.domain.merchants.model.CreateMerchantDomain
import com.dicoding.core.domain.merchants.model.GetMerchantByIdDomain
import com.dicoding.core.domain.merchants.model.MerchantResultDomain
import com.dicoding.core.domain.merchants.model.UpdateMerchantDomain
import com.dicoding.core.domain.merchants.repository.IMerchantRepository
import com.dicoding.core.utils.datamapper.MerchantDataMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// data/repository/MerchantRepository.kt
class MerchantRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IMerchantRepository {

    override fun createMerchant(request: CreateMerchantRequest): Flow<Resource<CreateMerchantDomain>> {
        return object : NetworkBoundResource<CreateMerchantDomain, CreateMerchantResponse>() {
            override suspend fun fetchFromApi(response: CreateMerchantResponse): CreateMerchantDomain {
                return MerchantDataMapper.mapCreateMerchantResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<CreateMerchantResponse>> {
                return remoteDataSource.createMerchant(request)
            }
        }.asFlow()
    }

    override fun getMerchants(): Flow<PagingData<MerchantResultDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { MerchantsPagingSource(remoteDataSource) }
        ).flow
    }

    override fun getMerchantById(id: String): Flow<Resource<GetMerchantByIdDomain>> {
        return object : NetworkBoundResource<GetMerchantByIdDomain, GetMerchantsByIdResponse>() {
            override suspend fun fetchFromApi(response: GetMerchantsByIdResponse): GetMerchantByIdDomain {
                return MerchantDataMapper.mapGetMerchantByIdResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<GetMerchantsByIdResponse>> {
                return remoteDataSource.getMerchantById(id)
            }
        }.asFlow()
    }

    override fun updateMerchant(id: String, request: CreateMerchantRequest): Flow<Resource<UpdateMerchantDomain>> {
        return object : NetworkBoundResource<UpdateMerchantDomain, UpdateMerchantResponse>() {
            override suspend fun fetchFromApi(response: UpdateMerchantResponse): UpdateMerchantDomain {
                return MerchantDataMapper.mapUpdateMerchantResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<UpdateMerchantResponse>> {
                return remoteDataSource.updateMerchant(id, request)
            }
        }.asFlow()
    }

    override fun deleteMerchant(id: String): Flow<Resource<Unit>> {
        return object : NetworkBoundResource<Unit, Unit>() {
            override suspend fun fetchFromApi(response: Unit): Unit {
                return
            }

            override suspend fun createCall(): Flow<ApiResponse<Unit>> {
                return remoteDataSource.deleteMerchant(id)
            }
        }.asFlow()
    }
}