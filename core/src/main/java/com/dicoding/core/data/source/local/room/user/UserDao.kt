package com.dicoding.core.data.source.local.room.user

import androidx.room.*
import com.dicoding.core.data.source.local.entity.auth.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user ORDER BY id DESC LIMIT 1")
    fun getUser(): Flow<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

}