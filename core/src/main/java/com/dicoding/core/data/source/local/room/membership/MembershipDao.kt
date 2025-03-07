package com.dicoding.core.data.source.local.room.membership

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.core.data.source.local.entity.membership.MembershipEntity
import java.util.Date

@Dao
interface MembershipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(membership: MembershipEntity)

    @Query("SELECT * FROM membership WHERE isActive = 1 LIMIT 1")
    fun getActiveMembership(): LiveData<MembershipEntity?>

    @Query("SELECT * FROM membership WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveMembershipSync(): MembershipEntity?

    @Query("UPDATE membership SET isActive = :isActive WHERE id = :id")
    suspend fun updateActivationStatus(id: String, isActive: Boolean)

    // Add this query for deleting membership
    @Query("DELETE FROM membership WHERE id = :id")
    suspend fun deleteMembership(id: String)

    // Add this query for deleting all memberships (useful for logout)
    @Query("DELETE FROM membership")
    suspend fun deleteAllMemberships()

    @Query("UPDATE membership SET remainingCoupons = :remainingCoupons WHERE id = :membershipId")
    suspend fun updateRemainingCoupons(membershipId: String, remainingCoupons: Int)
}
