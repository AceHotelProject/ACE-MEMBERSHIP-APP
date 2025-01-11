package com.dicoding.core.data.source.local.room.promo

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dicoding.core.data.source.local.entity.promo.PromoEntity

@Database(entities = [PromoEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class PromoDatabase : RoomDatabase() {
    abstract fun promoDao(): PromoDao
}