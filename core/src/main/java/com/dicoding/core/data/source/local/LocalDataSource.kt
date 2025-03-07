package com.dicoding.core.data.source.local

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.dicoding.core.data.source.local.entity.FavoriteStoryEntity
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import com.dicoding.core.data.source.local.room.membership.MembershipDao
import com.dicoding.core.data.source.local.room.test.StoryDao
import com.dicoding.core.data.source.local.room.user.UserDao
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.utils.datamapper.MembershipDataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val storyDao: StoryDao,
    private val userDao: UserDao,
    private val membershipDao: MembershipDao
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

    //////////////////////////////////////////////////////////////////// MEMBERSHIP
    // 1. Get active membership as LiveData
    fun getActiveMembership(): LiveData<MembershipLocal?> {
        return membershipDao.getActiveMembership()
            .map { entity -> entity?.let { MembershipDataMapper.toDomain(it) } }
    }

    // 2. Save membership to database
    suspend fun saveMembership(membership: MembershipLocal) = withContext(Dispatchers.IO) {
        val entity = MembershipDataMapper.toEntity(membership)
        membershipDao.insertMembership(entity)
    }

    // 3. Check if membership is expired
    suspend fun checkMembershipExpiry(): Boolean = withContext(Dispatchers.IO) {
        val membership = membershipDao.getActiveMembershipSync()

        if (membership != null && membership.expiryDateMillis != null) {
            val today = Date().time
            val isExpired = membership.expiryDateMillis!! < today

            // If expired, update status
            if (isExpired && membership.isActive) {
                membershipDao.updateActivationStatus(membership.id, false)
            }

            return@withContext isExpired
        }

        // No active membership or no expiry date
        return@withContext true
    }

    suspend fun deleteMembership(id: String) {
        membershipDao.deleteMembership(id)
    }

    suspend fun deleteAllMemberships() {
        membershipDao.deleteAllMemberships()
    }

    suspend fun updateRemainingCoupons(membershipId: String, remainingCoupons: Int) {
        membershipDao.updateRemainingCoupons(membershipId, remainingCoupons)
    }
}
