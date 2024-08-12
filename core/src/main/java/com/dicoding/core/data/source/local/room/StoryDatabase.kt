package com.dicoding.membership.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.core.data.source.local.entity.FavoriteStoryEntity
import com.dicoding.core.data.source.local.room.StoryDao

@Database(entities = [FavoriteStoryEntity::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
}