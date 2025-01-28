package com.dicoding.core.domain.file.interactor

import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.file.model.FileDeleteDomain
import com.dicoding.core.domain.file.model.FileUploadDomain
import com.dicoding.core.domain.file.repository.IFileRepository
import com.dicoding.core.domain.file.usecase.FileUseCase
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class FileInteractor @Inject constructor(
    private val fileRepository: IFileRepository
) : FileUseCase {
    override fun uploadFile(file: MultipartBody.Part): Flow<Resource<FileUploadDomain>> =
        fileRepository.uploadFile(file)

    override fun deleteFile(fileId: String): Flow<Resource<FileDeleteDomain>> =
        fileRepository.deleteFile(fileId)
}