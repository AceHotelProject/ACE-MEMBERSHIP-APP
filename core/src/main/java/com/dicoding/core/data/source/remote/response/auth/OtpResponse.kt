package com.dicoding.core.data.source.remote.response.auth

import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @field:SerializedName("message")
    val message: String? = null
)