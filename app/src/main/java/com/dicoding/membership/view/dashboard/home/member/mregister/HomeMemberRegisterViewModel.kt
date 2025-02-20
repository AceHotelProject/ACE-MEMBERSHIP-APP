package com.dicoding.membership.view.dashboard.home.member.mregister

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.LoginDomain
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.file.model.FileUploadDomain
import com.dicoding.core.domain.file.usecase.FileUseCase
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
class HomeMemberRegisterViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase,
    private val fileUseCase: FileUseCase // Add FileUseCase
): ViewModel() {
    var imagePath: String? = null
    private val _userData = MutableLiveData<LoginDomain>()
    val userData: LiveData<LoginDomain> = _userData

    fun setImageString(path: String) {
        imagePath = path
    }

    fun getUserData() {
        viewModelScope.launch {
            authUseCase.getUser()
                .collect { loginDomain ->
                    _userData.value = loginDomain
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

    fun completeUserData(
        id: String,
        name: String? = null,
        pathKTP: String? = null,
        citizenNumber: String? = null,
        phone: String? = null,
        address: String? = null,
        memberType: String? = null
    ) = userUseCase.completeUserData(
        id = id,
        name = name,
        pathKTP = pathKTP,
        citizenNumber = citizenNumber,
        phone = phone,
        address = address,
        subscriptionType = memberType
    ).asLiveData()
}