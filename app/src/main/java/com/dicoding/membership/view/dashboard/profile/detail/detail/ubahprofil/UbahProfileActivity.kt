package com.dicoding.membership.view.dashboard.profile.detail.detail.ubahprofil

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityUbahProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class UbahProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUbahProfileBinding
    private val viewModel: UbahProfileViewModel by viewModels()

    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCloseButton()
        handleButtonSave()
    }

    private fun setupCloseButton(){
        binding.btnClose.setOnClickListener{
            finish()
        }
    }

    //camera and gallery
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with the action
            showImagePickerDialog()
        } else {
            // Permission denied
            showToast("Permission required to access media")
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkAndRequestPermission(permission: String, action: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                action()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // Show explanation why permission is needed
                AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("This permission is needed to access media")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncher.launch(permission)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                // Request permission
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            // Convert gallery URI to bitmap then to our stored URI format
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedUri)
                val imageUri = getBitmapUri(bitmap)
                viewModel.setImageString(imageUri.toString())

                Glide.with(this)
                    .load(bitmap)
                    .centerCrop()
                    .into(binding.addMitraFotoMitraImage)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to process image")
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // Convert bitmap to URI using content provider
            val imageUri = getBitmapUri(bitmap)
            viewModel.setImageString(imageUri.toString())

            // Display image
            Glide.with(this)
                .load(bitmap)
                .centerCrop()
                .into(binding.addMitraFotoMitraImage)
        }
    }

    private fun getBitmapUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> captureImage()
                    1 -> openGallery()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun captureImage() {
        cameraLauncher.launch(null)
    }

    private fun handleButtonSave() {
        binding.apply {
            btnSave.setOnClickListener {
                val id = intent.getStringExtra(USER_ID) ?: ""
                val idPicturePath = viewModel.getImageString() ?: "EmptyPath"
                val name = tiNama.text?.toString() ?: "Default Name"
                val citizenNumber = tiNik.text?.toString() ?: "0000000000000000"
                val phone = tiTelepon.text?.toString() ?: "000000000000"
                val address = tiTipeAlamat.text?.toString() ?: "Default Address"

                //for debugging
                println("""
    ====== UPDATE USER REQUEST DATA ======
    ID: $id
    Image Path: $idPicturePath
    Name: $name
    Citizen Number: $citizenNumber
    Phone: $phone
    Address: $address
    ====================================
""".trimIndent())

                viewModel.updateUserData(
                    id = id,
                    idPicturePath = idPicturePath,
                    name = name,
                    citizenNumber = citizenNumber,
                    phone = phone,
                    address = address
                ).observe(this@UbahProfileActivity) { result ->
                    when (result) {
                        is Resource.Error -> {
                            showLoading(false)
                            isButtonEnabled(true)

                            if (!isInternetAvailable(this@UbahProfileActivity)) {
                                showToast(getString(R.string.check_internet))
                            } else {
                                showToast(result.message.toString())
                            }
                        }

                        is Resource.Loading -> {
                            showLoading(true)
                            isButtonEnabled(false)
                        }

                        is Resource.Message -> {
                            showLoading(false)
                            isButtonEnabled(true)
                        }

                        is Resource.Success -> {
                            showLoading(false)
                            isButtonEnabled(true)

                            showToast("Profile berhasil diperbarui")
                            finish()
                        }

                        else -> {}
                    }
                }
            }
        }
        binding.addMitraFotoMitraImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkAndRequestPermission(CAMERA_PERMISSION) {
                    checkAndRequestPermission(STORAGE_PERMISSION) {
                        showImagePickerDialog()
                    }
                }
            } else {
                // For older Android versions, permissions are granted at install time
                showImagePickerDialog()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Implement your loading UI here
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnSave.isEnabled = isEnabled
    }

    companion object {
        const val USER_ID = "user_id"
    }
}