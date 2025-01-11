package com.dicoding.core.data.source.local.entity.promo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promos")
data class PromoEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val detail: String,
    val pictures: List<String>,
    val tnc: List<String>,
    val startDate: String,
    val endDate: String,
    val memberType: String,
    val merchantId: String,
    val maximalUse: Int,
    val used: Int,
    val isActive: Boolean,
    val createdBy: String?,
    val updatedBy: String?,
    val token: String?
)