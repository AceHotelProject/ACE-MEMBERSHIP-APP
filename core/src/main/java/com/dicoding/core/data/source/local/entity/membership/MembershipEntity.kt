package com.dicoding.core.data.source.local.entity.membership

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "membership")
data class MembershipEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",

    @ColumnInfo(name = "userId")
    var userId: String = "",

    @ColumnInfo(name = "type")
    var type: String? = null,

    @ColumnInfo(name = "duration")
    var duration: Int? = null,

    @ColumnInfo(name = "price")
    var price: Long? = null,

    @ColumnInfo(name = "maxCoupon")
    var maxCoupon: Int? = null,

    @ColumnInfo(name = "createdAt")
    var createdAt: String? = null,

    // Store complex types as JSON strings
    @ColumnInfo(name = "imageJson")
    var imageJson: String? = null,

    @ColumnInfo(name = "tncJson")
    var tncJson: String? = null,

    // Additional fields for tracking
    @ColumnInfo(name = "purchaseDateMillis")
    var purchaseDateMillis: Long? = null,

    @ColumnInfo(name = "expiryDateMillis")
    var expiryDateMillis: Long? = null,

    @ColumnInfo(name = "isActive")
    var isActive: Boolean = false,

    @ColumnInfo(name = "remainingCoupons")
    var remainingCoupons: Int? = null
) : Parcelable