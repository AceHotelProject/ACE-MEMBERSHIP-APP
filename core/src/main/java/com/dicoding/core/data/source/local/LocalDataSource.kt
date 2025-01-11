package com.dicoding.core.data.source.local

import android.util.Log
import androidx.paging.PagingSource
import com.dicoding.core.data.source.local.entity.FavoriteStoryEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.local.entity.promo.PromoEntity
import com.dicoding.core.data.source.local.room.promo.PromoDao
import com.dicoding.core.data.source.local.room.test.StoryDao
import com.dicoding.core.data.source.local.room.user.UserDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val storyDao: StoryDao,
    private val userDao: UserDao,
    private val promoDao: PromoDao
) {

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

    ////////////////////////////////////////////////////////////////////

    fun getUser(): Flow<UserEntity?> {
        Log.d("LocalDataSource", "Getting user data")
        return userDao.getUser()
    }

    suspend fun insertUser(user: UserEntity) {
        Log.d("LocalDataSource", "Inserting user: $user")
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

    ////////////////////////////////////////////////////////////////////
    fun getPromos(): PagingSource<Int, PromoEntity> = promoDao.getPromos()

    suspend fun insertPromos(promos: List<PromoEntity>) {
        promoDao.insertPromos(promos)
    }

    suspend fun clearPromos() {
        promoDao.clearPromos()
    }
}
