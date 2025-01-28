package com.dicoding.core.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.utils.datamapper.PromoDataMapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PromosPagingSource @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val category: String = "",
    private val status: String = "",
    private val name: String = ""
) : PagingSource<Int, PromoDomain>() {

    override fun getRefreshKey(state: PagingState<Int, PromoDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PromoDomain> {
        return try {
            val position = params.key ?: START_PAGE_INDEX

            val response = remoteDataSource.getPromos(
                page = position,
                limit = params.loadSize,
                category = category,
                status = status,
                name = name
            ).first()

            when (response) {
                is ApiResponse.Success -> {
                    val promoDomain = PromoDataMapper.mapGetPromoResponseToDomain(response.data)
                    LoadResult.Page(
                        data = promoDomain.results,
                        prevKey = if (position == START_PAGE_INDEX) null else position - 1,
                        nextKey = if (position >= promoDomain.totalPages) null else position + 1
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