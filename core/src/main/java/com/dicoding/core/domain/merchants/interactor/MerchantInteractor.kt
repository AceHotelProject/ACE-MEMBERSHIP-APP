package com.dicoding.core.domain.merchants.interactor

import androidx.paging.PagingData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantRequest
import com.dicoding.core.data.source.remote.response.merchants.MerchantData
import com.dicoding.core.domain.merchants.model.CreateMerchantDomain
import com.dicoding.core.domain.merchants.model.GetMerchantByIdDomain
import com.dicoding.core.domain.merchants.model.MerchantResultDomain
import com.dicoding.core.domain.merchants.model.UpdateMerchantDomain
import com.dicoding.core.domain.merchants.repository.IMerchantRepository
import com.dicoding.core.domain.merchants.usecase.MerchantUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MerchantInteractor @Inject constructor(
    private val merchantRepository: IMerchantRepository
) : MerchantUseCase {
    override fun createMerchant(request: CreateMerchantRequest): Flow<Resource<CreateMerchantDomain>> =
        merchantRepository.createMerchant(request)

    override fun getMerchants(): Flow<PagingData<MerchantResultDomain>> =
        merchantRepository.getMerchants()

    override fun getMerchantById(id: String): Flow<Resource<GetMerchantByIdDomain>> =
        merchantRepository.getMerchantById(id)

    override fun updateMerchant(id: String, request: MerchantData): Flow<Resource<UpdateMerchantDomain>> =
        merchantRepository.updateMerchant(id, request)

    override fun deleteMerchant(id: String): Flow<Resource<Unit>> =
        merchantRepository.deleteMerchant(id)
}