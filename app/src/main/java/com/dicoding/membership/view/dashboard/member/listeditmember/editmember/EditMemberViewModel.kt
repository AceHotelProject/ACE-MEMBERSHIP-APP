package com.dicoding.membership.view.dashboard.member.listeditmember.editmember

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//changed the resource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.file.model.FileDeleteDomain
import com.dicoding.core.domain.file.model.FileUploadDomain
import com.dicoding.core.domain.file.usecase.FileUseCase
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.max


@HiltViewModel
class EditMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase,
    private val fileUseCase: FileUseCase
) : ViewModel() {

    private val _membershipData = MutableStateFlow<Resource<Membership>?>(null)
    val membershipData: StateFlow<Resource<Membership>?> = _membershipData.asStateFlow()

    private val _membershipState = MutableStateFlow<Resource<Membership>?>(null)
    val membershipState: StateFlow<Resource<Membership>?> = _membershipState.asStateFlow()

    fun getMembershipById(id: String) {
        viewModelScope.launch {
            membershipUseCase.getMembershipById(id)
                .collect { result ->
                    _membershipData.value = result
                }
        }
    }

    fun createMembership(type: String, duration: Int, maxCoupon: Int, price: Int, tnc: List<String>, image: List<String>) {
        viewModelScope.launch {
            _membershipState.value = Resource.Loading()
            membershipUseCase.createMembership(type, duration, maxCoupon, price, tnc, image)
                .collect { result ->
                    _membershipState.value = result
                }
        }
    }

    fun updateMembership(id: String, type: String, maxCoupon: Int, duration: Int, price: Int, tnc: List<String>, image: List<String>) {
        viewModelScope.launch {
            membershipUseCase.updateMembership(
                id = id,
                type = type,
                maxCoupon = maxCoupon,
                duration = duration,
                price = price,
                tnc = tnc,
                image = image
            ).collect { result ->
                _membershipState.value = result  // This is correct way to update StateFlow
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

    fun deleteFile(fileUrl: String): Flow<Resource<FileDeleteDomain>> {
        return flow {
            emit(Resource.Loading())
            try {
                fileUseCase.deleteFile(fileUrl).collect { result ->
                    emit(result)
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun compressImage(context: Context, uri: Uri): File? {
        try {
            // Create input stream from Uri
            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                val bufferedInputStream = inputStream.buffered()
                bufferedInputStream.mark(inputStream.available())

                // First decode with inJustDecodeBounds=true to check dimensions
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(bufferedInputStream, null, options)

                // Reset stream to start
                bufferedInputStream.reset()

                // Calculate inSampleSize
                options.apply {
                    inJustDecodeBounds = false
                    inSampleSize = calculateInSampleSize(this, 1024, 1024) // Max dimensions 1024x1024
                }

                // Decode bitmap with calculated inSampleSize
                val bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options)

                // Compress bitmap
                val outputStream = ByteArrayOutputStream()
                var quality = 100
                bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

                // Keep compressing until size is under 1MB or quality hits minimum
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

                bitmap?.recycle() // Clean up bitmap
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

            // Calculate largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than requested height and width
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun convertFileToMultipart(file: File): MultipartBody.Part? {
        return try {
            // Create RequestBody from file
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            // Create MultipartBody.Part using the RequestBody
            MultipartBody.Part.createFormData(
                name = "file", // The key name expected by your server
                filename = file.name,
                body = requestFile
            )
        } catch (e: Exception) {
            Log.e("FileConversion", "Error converting to MultipartBody: ${e.message}")
            null
        }
    }
}