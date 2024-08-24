package com.dicoding.membership.core.domain.story.tester.interactor

import androidx.paging.PagingData
import com.dicoding.core.data.repository.StoryRepositoryTester
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.core.domain.story.tester.usecase.StoryUseCaseTester
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoryInteractorTester @Inject constructor(
    private val storyRepository: StoryRepositoryTester
) : StoryUseCaseTester {

    override fun getStories(
        filterDate: String,
        isFinished: Boolean
    ): Flow<PagingData<StoryDomainTester>> {
        return storyRepository.getStories(filterDate, isFinished)
    }

    override fun getDetailStories(id: String): Flow<Resource<StoryDomainTester>> {
        return storyRepository.getDetailStories(id)
    }

    override fun getFavoriteStories(): Flow<List<StoryDomainTester>> {
        return storyRepository.getFavoriteStories()
    }

    override suspend fun getFavoriteStoryById(id: String): StoryDomainTester? {
        return storyRepository.getFavoriteStoryById(id)
    }

    override suspend fun insertFavoriteStory(story: StoryDomainTester) {
        storyRepository.insertFavoriteStory(story)
    }

    override suspend fun deleteFavoriteStory(story: StoryDomainTester) {
        storyRepository.deleteFavoriteStory(story)
    }
}