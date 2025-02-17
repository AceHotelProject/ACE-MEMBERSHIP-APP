package com.dicoding.membership.view.dashboard.profile.detail.detail.ubahprofil

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityUbahProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UbahProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUbahProfileBinding
    private val viewModel: UbahProfileViewModel by viewModels()
    private var userData: User? = null
    private var selectedImageUri: Uri? = null
    private var isImageSelected = false
    private var previousImagePath: String? = null

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

        userData = intent.getParcelableExtra(USER_DATA)
        previousImagePath = userData?.pathKTP

        setupInitialData()
        setupImageHandling()
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
            selectedImageUri = selectedUri  // Just store the URI
            // Show image locally
            Glide.with(this)
                .load(selectedUri)
                .centerCrop()
                .into(binding.addFotoKtpImage)
            isImageSelected = true
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // Convert bitmap to URI for local storage
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                "Title",
                null
            )
            selectedImageUri = Uri.parse(path)  // Store the URI
            // Show image locally
            Glide.with(this)
                .load(bitmap)
                .centerCrop()
                .into(binding.addFotoKtpImage)
            isImageSelected = true
        }
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

    private fun setupInitialData() {
        userData?.let { user ->
            binding.apply {
                tiNama.setText(user.name)
                tiNik.setText(user.citizenNumber ?: "")
                tiTelepon.setText(user.phone ?: "")
                tiTipeAlamat.setText(user.address ?: "")

                // Handle KTP image path if exists
                user.pathKTP?.let { path ->
                    previousImagePath = path
                    Glide.with(this@UbahProfileActivity)
                        .load(path)
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(addFotoKtpImage)
                }
            }
        }
    }

    private fun setupImageHandling() {
        binding.addFotoKtpImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkAndRequestPermission(CAMERA_PERMISSION) {
                    checkAndRequestPermission(STORAGE_PERMISSION) {
                        showImagePickerDialog()
                    }
                }
            } else {
                showImagePickerDialog()
            }
        }
    }

    private fun handleButtonSave() {
        binding.btnSave.setOnClickListener {
            // Show loading immediately when button is pressed
            showLoading(true)

            userData?.let { user ->
                val name = binding.tiNama.text?.toString() ?: user.name
                val citizenNumber = binding.tiNik.text?.toString() ?: user.citizenNumber ?: ""
                val phone = binding.tiTelepon.text?.toString() ?: user.phone ?: ""
                val address = binding.tiTipeAlamat.text?.toString() ?: user.address ?: ""

                lifecycleScope.launch {
                    // If new image is selected, handle upload
                    if (isImageSelected && selectedImageUri != null) {
                        // Try to delete old image first
                        if (previousImagePath != null && previousImagePath != "EmptyPath") {
                            try {
                                viewModel.deleteFile(previousImagePath!!).collect { result ->
                                    when (result) {
                                        is Resource.Success -> Log.d("ImageDelete", "Successfully deleted old image")
                                        is Resource.Error -> Log.e("ImageDelete", "Failed to delete old image: ${result.message}")
                                        else -> {}
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("ImageDelete", "Error during deletion: ${e.message}")
                            }
                        }

                        // Upload new image
                        viewModel.uploadFile(selectedImageUri!!, this@UbahProfileActivity)
                            .collect { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        result.data?.let { upload ->
                                            // After successful upload, update user data
                                            updateUserData(
                                                user = user,
                                                name = name,
                                                citizenNumber = citizenNumber,
                                                phone = phone,
                                                address = address,
                                                newImageUrl = upload.fileUrl
                                            )
                                        }
                                    }
                                    is Resource.Error -> {
                                        showLoading(false)
                                        showToast("Upload failed: ${result.message}")
                                    }
                                    else -> {}
                                }
                            }
                    } else {
                        // No new image, just update user data with existing image path
                        updateUserData(
                            user = user,
                            name = name,
                            citizenNumber = citizenNumber,
                            phone = phone,
                            address = address,
                            newImageUrl = previousImagePath ?: "EmptyPath"
                        )
                    }
                }
            }
        }
    }

    private fun updateUserData(
        user: User,
        name: String,
        citizenNumber: String,
        phone: String,
        address: String,
        newImageUrl: String
    ) {
        viewModel.updateUserData(
            id = user.id,
            idPicturePath = newImageUrl,
            name = name,
            citizenNumber = citizenNumber,
            phone = phone,
            address = address
        ).observe(this@UbahProfileActivity) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    showToast("Profile berhasil diperbarui")
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    if (!isInternetAvailable(this@UbahProfileActivity)) {
                        showToast(getString(R.string.check_internet))
                    } else {
                        showToast(result.message.toString())
                    }
                }
                else -> {} // Don't hide loading for other states
            }
        }
    }

    private fun handleImageUpload(uri: Uri) {
        lifecycleScope.launch {
            // Directly proceed with upload, no deletion here
            proceedWithUpload(uri)
        }
    }

    private fun proceedWithUpload(uri: Uri) {
        lifecycleScope.launch {
            viewModel.uploadFile(uri, this@UbahProfileActivity)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            result.data?.let { upload ->
                                selectedImageUri = Uri.parse(upload.fileUrl)
                                previousImagePath = upload.fileUrl // Update the previous path
                                isImageSelected = true
                                viewModel.setImageString(upload.fileUrl)

                                // Display the selected image
                                Glide.with(this@UbahProfileActivity)
                                    .load(uri)
                                    .centerCrop()
                                    .into(binding.addFotoKtpImage)
                            }
                            showLoading(false)
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            showToast("Upload failed: ${result.message}")
                        }
                        else -> {
                            showLoading(false)
                        }
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                handleImageUpload(uri)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Implement your loading UI here
        binding.loadingOverlay.visibility = if(isLoading) View.VISIBLE else View.GONE
        binding.svBody.visibility = if(!isLoading) View.VISIBLE else View.GONE
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnSave.isEnabled = isEnabled
    }

    companion object {
        const val USER_DATA = "user_data"
        private const val IMAGE_PICK_CODE = 1000
    }
}