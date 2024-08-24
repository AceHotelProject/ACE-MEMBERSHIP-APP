package com.dicoding.core.data.source.remote.response.test

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailStoryResponse(
    val error: Boolean? = null,
    val message: String? = null,
    val story: Story? = null
) : Parcelable

@Parcelize
data class Story(
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val name: String? = null,
    val description: String? = null,
    val lon: Double? = null,
    val id: String? = null,
    val lat: Double? = null
) : Parcelable