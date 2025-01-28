package com.dicoding.core.data.source.remote.response.file

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileUploadResponse(
    @SerializedName("urls")
    val urls: List<String> = emptyList()
): Parcelable {
    val fileUrl: String
        get() = urls.firstOrNull() ?: ""
}

@Parcelize
data class FileDeleteResponse(
    @SerializedName("success")
    val success: Boolean
): Parcelable

@Parcelize
data class FileDeleteRequest(
    @SerializedName("fileId")
    val fileId: String
): Parcelable
