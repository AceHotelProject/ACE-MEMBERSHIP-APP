package com.dicoding.core.domain.user.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String,
    //val is validated
    val phone: String,
    val address: String,
    val citizen_number: String,
    val id_picture_path: String,
    val role: String,
    val merchant_id: String,
    val android_id: String,
    //val member_type
    val coupon_used: Int,
    val point: Int,
    val subscription_start_date: String,
    val subscription_end_date: String

) : Parcelable
