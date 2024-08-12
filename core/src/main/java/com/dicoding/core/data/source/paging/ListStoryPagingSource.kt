package com.dicoding.core.data.source.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.core.data.source.remote.network.ApiService
import com.dicoding.core.data.source.remote.response.test.StoryResponse
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.membership.core.utils.constants.FilterDate
import com.dicoding.membership.core.utils.datamapper.StoryDataMapper
import javax.inject.Inject

class ListStoryPagingSource @Inject constructor(
    private val apiService: ApiService,
    private val filterDate: String,
    private val isFinished: Boolean = false
) : PagingSource<Int, StoryDomainTester>() {

    override fun getRefreshKey(state: PagingState<Int, StoryDomainTester>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryDomainTester> {
        return try {
            val position = params.key ?: START_PAGE_INDEX

            val response: StoryResponse = apiService.getStories(
                page = position,
                size = params.loadSize
            )

            val listStory = StoryDataMapper.mapListStoryToDomain(response.listStory)

            val filteredList = when (filterDate) {
                FilterDate.NOW.value -> {
                    if (isFinished) {
                        listStory.filter { it.createdAt != null && it.createdAt != "Empty" }
                    } else {
                        listStory.filter { it.createdAt == null || it.createdAt == "Empty" }
                    }
                }
                FilterDate.MONTH.value -> {
                    if (isFinished) {
                        listStory.filter { it.createdAt != null && it.createdAt != "Empty" }
                    } else {
                        listStory.filter { it.createdAt == null || it.createdAt == "Empty" }
                    }
                }
                FilterDate.YEAR.value -> {
                    if (isFinished) {
                        listStory.filter { it.createdAt != null && it.createdAt != "Empty" }
                    } else {
                        listStory.filter { it.createdAt == null || it.createdAt == "Empty" }
                    }
                }
                else -> {
                    listStory
                }
            }

            LoadResult.Page(
                data = filteredList,
                prevKey = if (position == START_PAGE_INDEX) null else position - 1,
                nextKey = if (filteredList.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val START_PAGE_INDEX = 1
    }
}