package com.dicoding.membership.view.status

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusTemplate(
    val title: String,
    val description: String,
    val showCoupon: Boolean = false,
    val promoCode: String = "",
    val expiryTime: String = "",
    val buttonText: String = "Selesai"
) : Parcelable