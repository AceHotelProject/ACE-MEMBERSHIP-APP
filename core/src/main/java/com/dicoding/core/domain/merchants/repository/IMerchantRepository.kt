package com.dicoding.core.domain.merchants.repository

import androidx.paging.PagingData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantRequest
import com.dicoding.core.domain.merchants.model.CreateMerchantDomain
import com.dicoding.core.domain.merchants.model.GetMerchantByIdDomain
import com.dicoding.core.domain.merchants.model.GetMerchantsDomain
import com.dicoding.core.domain.merchants.model.MerchantDomain
import com.dicoding.core.domain.merchants.model.MerchantResultDomain
import com.dicoding.core.domain.merchants.model.UpdateMerchantDomain
import kotlinx.coroutines.flow.Flow

interface IMerchantRepository {

    fun createMerchant(request: CreateMerchantRequest): Flow<Resource<CreateMerchantDomain>>

    fun getMerchants(): Flow<PagingData<MerchantResultDomain>>

    fun getMerchantById(id: String): Flow<Resource<GetMerchantByIdDomain>>

    fun updateMerchant(id: String, request: CreateMerchantRequest): Flow<Resource<UpdateMerchantDomain>>

    fun deleteMerchant(id: String): Flow<Resource<Unit>>
}
