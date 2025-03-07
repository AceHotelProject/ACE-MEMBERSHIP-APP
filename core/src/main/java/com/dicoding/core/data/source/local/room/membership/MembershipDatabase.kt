package com.dicoding.core.data.source.local.room.membership

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.core.data.source.local.entity.membership.MembershipEntity

@Database(entities = [MembershipEntity::class], version = 2, exportSchema = false)
abstract class MembershipDatabase : RoomDatabase() {
    abstract fun membershipDao(): MembershipDao

    companion object {
        @Volatile
        private var INSTANCE: MembershipDatabase? = null

        fun getDatabase(context: Context): MembershipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MembershipDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}