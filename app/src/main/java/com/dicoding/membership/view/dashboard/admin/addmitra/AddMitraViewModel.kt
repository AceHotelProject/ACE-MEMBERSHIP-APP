package com.dicoding.membership.view.dashboard.admin.addmitra

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.file.model.FileDeleteDomain
import com.dicoding.core.domain.file.model.FileUploadDomain
import com.dicoding.core.domain.file.usecase.FileUseCase
import com.dicoding.core.domain.merchants.usecase.MerchantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AddMitraViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val fileUseCase: FileUseCase,
    private val merchantUseCase: MerchantUseCase
) : ViewModel() {

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun uploadFile(uri: Uri, context: Context): Flow<Resource<FileUploadDomain>> {
        return flow {
            emit(Resource.Loading())
            try {
                val compressedFile = compressImage(context, uri)
                if (compressedFile != null) {
                    Log.d("UploadFile", "Starting upload for URI: $uri with size: ${compressedFile.length() / 1024}KB")
                    val multipartFile = convertFileToMultipart(compressedFile)
                    if (multipartFile != null) {
                        fileUseCase.uploadFile(multipartFile).collect { result ->
                            emit(result)
                        }
                    } else {
                        emit(Resource.Error("Failed to process compressed image"))
                    }
                    // Clean up temporary file
                    compressedFile.delete()
                } else {
                    emit(Resource.Error("Failed to compress image"))
                }
            } catch (e: Exception) {
                Log.e("UploadFile", "Error: ${e.message}")
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun compressImage(context: Context, uri: Uri): File? {
        try {
            // Create a buffered input stream from Uri
            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                val bufferedInputStream = inputStream.buffered()
                bufferedInputStream.mark(inputStream.available())

                // Decode image size first
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(bufferedInputStream, null, options)

                // Reset stream
                bufferedInputStream.reset()

                // Calculate inSampleSize
                options.apply {
                    inJustDecodeBounds = false
                    inSampleSize = calculateInSampleSize(this, 1024, 1024) // Max dimensions 1024x1024
                }

                // Decode bitmap with inSampleSize
                val bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options)

                // Compress bitmap
                val outputStream = ByteArrayOutputStream()
                var quality = 100
                bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

                // Check if size is more than 1MB and compress until it's under 1MB
                while (outputStream.toByteArray().size > 1024 * 1024 && quality > 10) {
                    outputStream.reset()
                    quality -= 10
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                }

                // Create temporary file
                val tempFile = File.createTempFile("compressed_", ".jpg", context.cacheDir)
                FileOutputStream(tempFile).use { fos ->
                    fos.write(outputStream.toByteArray())
                }

                bitmap?.recycle()
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

    fun deleteFile(fileUrl: String): Flow<Resource<FileDeleteDomain>> {
        return flow {
            emit(Resource.Loading())
            try {
                Log.d("DeleteFile", "Deleting file: $fileUrl")
                fileUseCase.deleteFile(fileUrl).collect { result ->
                    emit(result)
                }
            } catch (e: Exception) {
                Log.e("DeleteFile", "Error: ${e.message}")
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    if (columnIndex != -1) cursor.getString(columnIndex) else null
                } else null
            }
        } catch (e: Exception) {
            Log.e("PathConversion", "Error: ${e.message}")
            null
        }
    }
}