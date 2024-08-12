package com.dicoding.membership.core.domain.auth.tester.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResponseDomain(
    val error: Boolean,
    val message: String
) : Parcelable