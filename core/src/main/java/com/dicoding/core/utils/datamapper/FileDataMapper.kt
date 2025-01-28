package com.dicoding.core.utils.datamapper

import com.dicoding.core.data.source.remote.response.file.FileDeleteResponse
import com.dicoding.core.data.source.remote.response.file.FileUploadResponse
import com.dicoding.core.domain.file.model.FileDeleteDomain
import com.dicoding.core.domain.file.model.FileUploadDomain

object FileDataMapper {
    fun mapUploadResponseToDomain(response: FileUploadResponse): FileUploadDomain =
        FileUploadDomain(fileUrl = response.fileUrl)

    fun mapDeleteResponseToDomain(success: Boolean): FileDeleteDomain =
        FileDeleteDomain(success = success)
}