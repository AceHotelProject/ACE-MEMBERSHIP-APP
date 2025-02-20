package com.dicoding.membership.view.dashboard.floatingvalidasi.detailvalidasi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.file.model.FileUploadDomain
import com.dicoding.core.domain.file.usecase.FileUseCase
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class DetailValidasiViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val fileUseCase: FileUseCase
): ViewModel(){
    private val _userData = MutableLiveData<Resource<User>>()
    val userData: LiveData<Resource<User>> = _userData
    private val _uploadState = MutableLiveData<Resource<FileUploadDomain>>()
    val uploadState: LiveData<Resource<FileUploadDomain>> = _uploadState

    var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null

    fun getUserData(userId: String) {
        viewModelScope.launch {
            // Emit loading state
            _userData.value = Resource.Loading()

            userUseCase.getUserData(userId)
                .catch { e ->
                    _userData.value = Resource.Error(e.message ?: "Nah")
                }
                .collect { result ->
                    Log.d("Debug View Model", "user ID: ${userId}")
                    _userData.value = result
                }
        }
    }

    fun uploadFile(uri: Uri, context: Context): Flow<Resource<FileUploadDomain>> {
        return flow {
            emit(Resource.Loading())
            try {
                val compressedFile = compressImage(context, uri)
                if (compressedFile != null) {
                    val multipartFile = convertFileToMultipart(compressedFile)
                    if (multipartFile != null) {
                        fileUseCase.uploadFile(multipartFile).collect { result ->
                            emit(result)
                        }
                    } else {
                        emit(Resource.Error("Failed to process compressed image"))
                    }
                    compressedFile.delete()
                } else {
                    emit(Resource.Error("Failed to compress image"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun compressImage(context: Context, uri: Uri): File? {
        try {
            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                val bufferedInputStream = inputStream.buffered()
                bufferedInputStream.mark(inputStream.available())

                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(bufferedInputStream, null, options)

                bufferedInputStream.reset()

                options.apply {
                    inJustDecodeBounds = false
                    inSampleSize = calculateInSampleSize(this, 1024, 1024)
                }

                val bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options)

                val outputStream = ByteArrayOutputStream()
                var quality = 100
                bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

                while (outputStream.toByteArray().size > 1024 * 1024 && quality > 10) {
                    outputStream.reset()
                    quality -= 10
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                }

                val tempFile = File.createTempFile("compressed_", ".jpg", context.cacheDir)
                FileOutputStream(tempFile).use { fos ->
                    fos.write(outputStream.toByteArray())
                }

                bitmap?.recycle()
                inputStream.close()
                return tempFile
            }
        } catch (e: Exception) {
            Log.e("ImageCompression", "Error compressing image: ${e.message}")
        }
        return null
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun convertFileToMultipart(file: File): MultipartBody.Part? {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("FileConversion", "Error converting to MultipartBody: ${e.message}")
            null
        }
    }

}