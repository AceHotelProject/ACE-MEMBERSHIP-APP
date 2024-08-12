package com.dicoding.core.data.source.local

import com.dicoding.core.data.source.local.entity.FavoriteStoryEntity
import com.dicoding.core.data.source.local.room.StoryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(private val storyDao: StoryDao) {

    fun getFavoriteStories(): Flow<List<FavoriteStoryEntity>> {
        return storyDao.getFavoriteStories()
    }

    suspend fun insertFavoriteStory(story: FavoriteStoryEntity) {
        storyDao.insertFavoriteStory(story)
    }

    suspend fun getFavoriteStoryById(id: String): FavoriteStoryEntity? {
        return storyDao.getFavoriteStoryById(id)
    }

    suspend fun deleteFavoriteStory(story: FavoriteStoryEntity) {
        storyDao.deleteFavoriteStory(story)
    }
}
