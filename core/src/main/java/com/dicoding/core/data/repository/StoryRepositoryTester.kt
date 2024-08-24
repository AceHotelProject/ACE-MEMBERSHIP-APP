package com.dicoding.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.local.LocalDataSource
import com.dicoding.core.data.source.paging.ListStoryPagingSource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.network.ApiService
import com.dicoding.core.data.source.remote.response.test.DetailStoryResponse
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.membership.core.domain.story.tester.repository.IStoryRepositoryTester
import com.dicoding.membership.core.utils.datamapper.StoryDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryTester @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val apiService: ApiService
) : IStoryRepositoryTester {

    override fun getStories(filterDate: String, isFinished: Boolean): Flow<PagingData<StoryDomainTester>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ListStoryPagingSource(
                    apiService,
                    filterDate,
                    isFinished) }
        ).flow
    }

    override fun getDetailStories(id: String): Flow<Resource<StoryDomainTester>> {
        return object : NetworkBoundResource<StoryDomainTester, DetailStoryResponse>() {
            override suspend fun fetchFromApi(response: DetailStoryResponse): StoryDomainTester {
                return StoryDataMapper.mapDetailStoryToDomain(response.story!!)
            }

            override suspend fun createCall(): Flow<ApiResponse<DetailStoryResponse>> {
                return remoteDataSource.getDetailStories(id)
            }
        }.asFlow()
    }

    override fun getFavoriteStories(): Flow<List<StoryDomainTester>> {
        return localDataSource.getFavoriteStories().map {
            StoryDataMapper.mapEntitiesToDomain(it)
        }
    }

    override suspend fun getFavoriteStoryById(id: String): StoryDomainTester? {
        val entity = localDataSource.getFavoriteStoryById(id)
        return entity?.let { StoryDataMapper.mapEntityToDomain(it) }
    }

    override suspend fun insertFavoriteStory(story: StoryDomainTester) {
        localDataSource.insertFavoriteStory(StoryDataMapper.mapDomainToEntity(story))
    }

    override suspend fun deleteFavoriteStory(story: StoryDomainTester) {
        localDataSource.deleteFavoriteStory(StoryDataMapper.mapDomainToEntity(story))
    }
}
