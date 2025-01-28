package com.dicoding.core.data.repository

import com.dicoding.core.data.source.NetworkBoundResource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.RemoteDataSource
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.response.file.FileUploadResponse
import com.dicoding.core.domain.file.model.FileDeleteDomain
import com.dicoding.core.domain.file.model.FileUploadDomain
import com.dicoding.core.domain.file.repository.IFileRepository
import com.dicoding.core.utils.datamapper.FileDataMapper
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class FileRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : IFileRepository {
    override fun uploadFile(file: MultipartBody.Part): Flow<Resource<FileUploadDomain>> {
        return object : NetworkBoundResource<FileUploadDomain, FileUploadResponse>() {
            override suspend fun fetchFromApi(response: FileUploadResponse): FileUploadDomain {
                return FileDataMapper.mapUploadResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<FileUploadResponse>> {
                return remoteDataSource.uploadFile(file)
            }
        }.asFlow()
    }

    override fun deleteFile(fileId: String): Flow<Resource<FileDeleteDomain>> {
        return object : NetworkBoundResource<FileDeleteDomain, Boolean>() {
            override suspend fun fetchFromApi(response: Boolean): FileDeleteDomain {
                return FileDataMapper.mapDeleteResponseToDomain(response)
            }

            override suspend fun createCall(): Flow<ApiResponse<Boolean>> {
                return remoteDataSource.deleteFile(fileId)
            }
        }.asFlow()
    }
}