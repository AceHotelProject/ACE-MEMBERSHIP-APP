package com.dicoding.core.domain.file.usecase

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.file.model.FileDeleteDomain
import com.dicoding.core.domain.file.model.FileUploadDomain
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface FileUseCase {
    fun uploadFile(file: MultipartBody.Part): Flow<Resource<FileUploadDomain>>
    fun deleteFile(fileId: String): Flow<Resource<FileDeleteDomain>>
}