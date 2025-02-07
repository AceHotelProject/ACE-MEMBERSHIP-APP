package com.dicoding.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.paging.PromoHistoryPagingSource
import com.dicoding.core.data.source.paging.PromosPagingSource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoResepsionisResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoUserResponse
import com.dicoding.core.data.source.remote.response.promo.CreatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.EditPromoResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoResponse
import com.dicoding.core.domain.promo.model.ActivatePromoResepsionisDomain
import com.dicoding.core.domain.promo.model.ActivatePromoUserDomain
import com.dicoding.core.domain.promo.model.GetPromosDomain
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.domain.promo.repository.IPromoRepository
import com.dicoding.core.utils.datamapper.PromoDataMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromoRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
) : IPromoRepository {

    override fun createPromo(
        name: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        maximalUse: Int,
    ): Flow<Resource<PromoDomain>> {
        return object : NetworkBoundResource<PromoDomain, CreatePromoResponse>() {
            override suspend fun fetchFromApi(response: CreatePromoResponse): PromoDomain {
                return PromoDataMapper.mapCreatePromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<CreatePromoResponse>> {
                return remoteDataSource.createPromo(
                    name, category, detail, pictures, tnc,
                    startDate, endDate, memberType,
                    maximalUse
                )
            }
        }.asFlow()
    }

    override fun getPromos(
        category: String,
        status: String,
        name: String
    ): Flow<PagingData<PromoDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = {
                PromosPagingSource(remoteDataSource, category, status, name)
            }
        ).flow
    }

    override fun getProposalPromos(): Flow<Resource<GetPromosDomain>> {
        return object : NetworkBoundResource<GetPromosDomain, GetPromoResponse>() {
            override suspend fun fetchFromApi(response: GetPromoResponse): GetPromosDomain {
                return PromoDataMapper.mapGetPromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<GetPromoResponse>> {
                return remoteDataSource.getProposalPromos()
            }
        }.asFlow()
    }

    override fun editPromo(
        id: String,
        name: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        maximalUse: Int,
        isActive: Boolean
    ): Flow<Resource<PromoDomain>> {
        return object : NetworkBoundResource<PromoDomain, EditPromoResponse>() {
            override suspend fun fetchFromApi(response: EditPromoResponse): PromoDomain {
                return PromoDataMapper.mapEditPromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<EditPromoResponse>> {
                return remoteDataSource.editPromo(
                    id, name, category, detail, pictures, tnc,
                    startDate, endDate, memberType, maximalUse, isActive
                )
            }
        }.asFlow()
    }

    override fun deletePromo(id: String): Flow<Resource<Unit>> {
        return object : NetworkBoundResource<Unit, Unit>() {
            override suspend fun fetchFromApi(response: Unit): Unit {
                return
            }

            override suspend fun createCall(): Flow<ApiResponse<Unit>> {
                return remoteDataSource.deletePromo(id)
            }
        }.asFlow()
    }

    override fun activatePromoResepsionis(id: String): Flow<Resource<ActivatePromoResepsionisDomain>> {
        return object : NetworkBoundResource<ActivatePromoResepsionisDomain, ActivatePromoResepsionisResponse>() {
            override suspend fun fetchFromApi(response: ActivatePromoResepsionisResponse): ActivatePromoResepsionisDomain {
                return PromoDataMapper.mapActivatePromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<ActivatePromoResepsionisResponse>> {
                return remoteDataSource.activatePromoResepsionis(id)
            }
        }.asFlow()
    }

    override fun activatePromoUser(id: String): Flow<Resource<ActivatePromoUserDomain>> {
        return object : NetworkBoundResource<ActivatePromoUserDomain, ActivatePromoUserResponse>() {
            override suspend fun fetchFromApi(response: ActivatePromoUserResponse): ActivatePromoUserDomain {
                return PromoDataMapper.mapActivatePromoUserResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<ActivatePromoUserResponse>> {
                return remoteDataSource.activatePromoUser(id)
            }
        }.asFlow()
    }

    override fun redeemPromo(token: String): Flow<Resource<Unit>> {
        return object : NetworkBoundResource<Unit, Unit>() {
            override suspend fun fetchFromApi(response: Unit): Unit {
                return
            }

            override suspend fun createCall(): Flow<ApiResponse<Unit>> {
                return remoteDataSource.redeemPromo(token)
            }
        }.asFlow()
    }

    override fun getPromoHistory(
        promoName: String,
        promoCategory: String,
        status: String
    ): Flow<PagingData<PromoHistoryDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = {
                PromoHistoryPagingSource(
                    remoteDataSource,
                    promoName,
                    promoCategory,
                    status
                )
            }
        ).flow
    }
}