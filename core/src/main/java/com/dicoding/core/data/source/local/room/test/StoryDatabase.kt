package com.dicoding.core.data.source.local.room.test

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.core.data.source.local.entity.FavoriteStoryEntity

@Database(entities = [FavoriteStoryEntity::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
}