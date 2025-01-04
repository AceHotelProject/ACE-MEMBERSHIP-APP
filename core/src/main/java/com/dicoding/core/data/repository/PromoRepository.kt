package com.dicoding.core.data.repository

import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.CreatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.DeletePromoResponse
import com.dicoding.core.data.source.remote.response.promo.EditPromoResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoHistoryResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoResponse
import com.dicoding.core.data.source.remote.response.promo.RedeemPromoResponse
import com.dicoding.core.domain.promo.interactor.ActivatePromoDomain
import com.dicoding.core.domain.promo.model.DeletePromoDomain
import com.dicoding.core.domain.promo.model.GetPromoHistoryDomain
import com.dicoding.core.domain.promo.model.GetPromosDomain
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.RedeemPromoDomain
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
        token: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        merchantId: String,
        maximalUse: Int,
        used: Int,
        isActive: Boolean
    ): Flow<Resource<PromoDomain>> {
        return object : NetworkBoundResource<PromoDomain, CreatePromoResponse>() {
            override suspend fun fetchFromApi(response: CreatePromoResponse): PromoDomain {
                return PromoDataMapper.mapCreatePromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<CreatePromoResponse>> {
                return remoteDataSource.createPromo(
                    name, token, category, detail, pictures, tnc,
                    startDate, endDate, memberType, merchantId,
                    maximalUse, used, isActive
                )
            }
        }.asFlow()
    }

    override fun getPromos(): Flow<Resource<GetPromosDomain>> {
        return object : NetworkBoundResource<GetPromosDomain, GetPromoResponse>() {
            override suspend fun fetchFromApi(response: GetPromoResponse): GetPromosDomain {
                return PromoDataMapper.mapGetPromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<GetPromoResponse>> {
                return remoteDataSource.getPromos()
            }
        }.asFlow()
    }

    override fun editPromo(
        id: String,
        token: String,
        name: String?,
        category: String?,
        detail: String?,
        pictures: List<String>?,
        tnc: List<String>?,
        startDate: String?,
        endDate: String?,
        memberType: String?,
        maximalUse: Int?,
        isActive: Boolean?
    ): Flow<Resource<PromoDomain>> {
        return object : NetworkBoundResource<PromoDomain, EditPromoResponse>() {
            override suspend fun fetchFromApi(response: EditPromoResponse): PromoDomain {
                return PromoDataMapper.mapEditPromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<EditPromoResponse>> {
                return remoteDataSource.editPromo(
                    id, token, name, category, detail, pictures, tnc,
                    startDate, endDate, memberType, maximalUse, isActive
                )
            }
        }.asFlow()
    }

    override fun deletePromo(id: String): Flow<Resource<DeletePromoDomain>> {
        return object : NetworkBoundResource<DeletePromoDomain, DeletePromoResponse>() {
            override suspend fun fetchFromApi(response: DeletePromoResponse): DeletePromoDomain {
                return PromoDataMapper.mapDeletePromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<DeletePromoResponse>> {
                return remoteDataSource.deletePromo(id)
            }
        }.asFlow()
    }

    override fun activatePromo(id: String, token: String): Flow<Resource<ActivatePromoDomain>> {
        return object : NetworkBoundResource<ActivatePromoDomain, ActivatePromoResponse>() {
            override suspend fun fetchFromApi(response: ActivatePromoResponse): ActivatePromoDomain {
                return PromoDataMapper.mapActivatePromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<ActivatePromoResponse>> {
                return remoteDataSource.activatePromo(id, token)
            }
        }.asFlow()
    }

    override fun redeemPromo(token: String, bearerToken: String): Flow<Resource<RedeemPromoDomain>> {
        return object : NetworkBoundResource<RedeemPromoDomain, RedeemPromoResponse>() {
            override suspend fun fetchFromApi(response: RedeemPromoResponse): RedeemPromoDomain {
                return PromoDataMapper.mapRedeemPromoResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<RedeemPromoResponse>> {
                return remoteDataSource.redeemPromo(token, bearerToken)
            }
        }.asFlow()
    }

    override fun getPromoHistory(token: String): Flow<Resource<GetPromoHistoryDomain>> {
        return object : NetworkBoundResource<GetPromoHistoryDomain, GetPromoHistoryResponse>() {
            override suspend fun fetchFromApi(response: GetPromoHistoryResponse): GetPromoHistoryDomain {
                return PromoDataMapper.mapGetPromoHistoryResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<GetPromoHistoryResponse>> {
                return remoteDataSource.getPromoHistory(token)
            }
        }.asFlow()
    }
}