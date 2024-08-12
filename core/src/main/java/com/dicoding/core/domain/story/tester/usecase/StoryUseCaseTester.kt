package com.dicoding.core.domain.story.tester.usecase

import androidx.paging.PagingData
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import kotlinx.coroutines.flow.Flow

interface StoryUseCaseTester {

    fun getStories(
        filterDate: String,
        isFinished: Boolean
    ): Flow<PagingData<StoryDomainTester>>

    fun getDetailStories(id: String): Flow<Resource<StoryDomainTester>>

    fun getFavoriteStories(): Flow<List<StoryDomainTester>>

    suspend fun getFavoriteStoryById(id: String): StoryDomainTester?

    suspend fun insertFavoriteStory(story: StoryDomainTester)

    suspend fun deleteFavoriteStory(story: StoryDomainTester)
}