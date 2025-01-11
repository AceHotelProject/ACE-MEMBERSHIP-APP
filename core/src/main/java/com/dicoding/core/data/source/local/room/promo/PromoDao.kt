package com.dicoding.core.data.source.local.room.promo

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.core.data.source.local.entity.promo.PromoEntity

@Dao
interface PromoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromos(promos: List<PromoEntity>)

    @Query("SELECT * FROM promos")
    fun getPromos(): PagingSource<Int, PromoEntity>

    @Query("DELETE FROM promos")
    suspend fun clearPromos()
}