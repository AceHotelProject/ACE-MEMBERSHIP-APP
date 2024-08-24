package com.dicoding.core.data.source.remote.response.test

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginTest(
    @field:SerializedName("loginResult")
    val loginResult: LoginResult? = null,
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null
) : Parcelable