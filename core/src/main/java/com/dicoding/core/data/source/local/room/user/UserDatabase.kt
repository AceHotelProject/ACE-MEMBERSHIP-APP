package com.dicoding.core.data.source.local.room.user

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.core.data.source.local.entity.auth.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}