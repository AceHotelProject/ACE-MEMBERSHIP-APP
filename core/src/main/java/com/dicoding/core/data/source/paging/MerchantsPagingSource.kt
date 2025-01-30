package com.dicoding.core.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.domain.merchants.model.MerchantResultDomain
import com.dicoding.core.utils.datamapper.MerchantDataMapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MerchantsPagingSource @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : PagingSource<Int, MerchantResultDomain>() {

    override fun getRefreshKey(state: PagingState<Int, MerchantResultDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MerchantResultDomain> {
        return try {
            val position = params.key ?: START_PAGE_INDEX

            val response = remoteDataSource.getMerchants(
                page = position,
                limit = params.loadSize
            ).first()

            when (response) {
                is ApiResponse.Success -> {
                    val merchantsDomain = MerchantDataMapper.mapGetMerchantsResponseToDomain(response.data)
                    LoadResult.Page(
                        data = merchantsDomain.results,
                        prevKey = if (position == START_PAGE_INDEX) null else position - 1,
                        nextKey = if (position >= merchantsDomain.totalPages) null else position + 1
                    )
                }
                is ApiResponse.Empty -> {
                    LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                }
                is ApiResponse.Error -> {
                    LoadResult.Error(Exception(response.errorMessage))
                }
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val START_PAGE_INDEX = 1
    }
}
