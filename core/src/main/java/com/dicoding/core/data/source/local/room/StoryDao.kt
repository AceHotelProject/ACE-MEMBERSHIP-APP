package com.dicoding.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.core.data.source.local.entity.FavoriteStoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteStory(story: FavoriteStoryEntity)

    @Query("SELECT * FROM favorite_stories WHERE id = :id")
    suspend fun getFavoriteStoryById(id: String): FavoriteStoryEntity?

    @Delete
    suspend fun deleteFavoriteStory(story: FavoriteStoryEntity)

    @Query("SELECT * FROM favorite_stories")
    fun getFavoriteStories(): Flow<List<FavoriteStoryEntity>>
}
