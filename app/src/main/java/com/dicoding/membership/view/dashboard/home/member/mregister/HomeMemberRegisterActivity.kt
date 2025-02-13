package com.dicoding.membership.view.dashboard.home.member.mregister

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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
import com.dicoding.membership.databinding.ActivityHomeMemberRegisterBinding
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class HomeMemberRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeMemberRegisterBinding
    private val viewModel: HomeMemberRegisterViewModel by viewModels()
    private var selectedPackage: String? = ""

    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedPackage = intent.getStringExtra(SELECTED_PACKAGE) ?: return
        handleImageButton()
        isButtonEnabled(false)
        handleEditText()
        handleButtonRegister()

        viewModel.getUserData()
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
                    .into(binding.imgKtpSim)
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
                .into(binding.imgKtpSim)
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

    private fun handleImageButton() {
        binding.imgKtpSim.setOnClickListener {
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

    private fun handleEditText() {
        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        })

        binding.edRegisterNik.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        })

        binding.edRegisterPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        })

        binding.edRegisterAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        })
    }

    private fun checkForms() {
        binding.apply {
            val name = edRegisterName.text.toString()
            val nik = edRegisterNik.text.toString()
            val phone = edRegisterPhone.text.toString()
            val address = edRegisterAddress.text.toString()

            // Name validation (not empty and minimum length)
            if (name.length < 3) {
                layoutRegisterName.error = "Nama harus minimal 3 karakter"
            } else {
                layoutRegisterName.error = null
            }

            // NIK validation (16 digits)
            if (nik.length != 16) {
                layoutRegisterNik.error = "NIK harus 16 digit"
            } else {
                layoutRegisterNik.error = null
            }

            // Phone validation (minimum length and starts with proper prefix)
            if (!isValidPhoneNumber(phone)) {
                layoutRegisterPhone.error = "Format nomor telepon tidak valid"
            } else {
                layoutRegisterPhone.error = null
            }

            // Address validation (not empty and minimum length)
            if (address.length < 10) {
                layoutRegisterAddress.error = "Alamat terlalu pendek"
            } else {
                layoutRegisterAddress.error = null
            }

            // Enable button only if all fields are valid
            isButtonEnabled(
                name.length >= 3 &&
                        nik.length == 16 &&
                        isValidPhoneNumber(phone) &&
                        address.length >= 10
            )
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Basic Indonesian phone number validation
        // Starts with 08 and has 10-13 digits
        return phone.matches(Regex("^08\\d{8,11}$"))
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnRegister.isEnabled = isEnabled
    }

    private fun showLoading(isLoading: Boolean) {
        // Add ProgressBar to your layout if needed
        binding.loadingOverlay.visibility = if(isLoading) View.VISIBLE else View.GONE
        binding.mainNsv.visibility = if(isLoading) View.GONE else View.VISIBLE
    }

    private fun handleButtonRegister() {
        binding.apply {
            btnRegister.setOnClickListener {
                // First observe userData to get the userId
                viewModel.userData.observe(this@HomeMemberRegisterActivity) { loginDomain ->
                    val userId = loginDomain.user.id
                    val idPicturePath = viewModel.imagePath ?: "EmptyPath"
                    val name = edRegisterName.text?.toString() ?: "Default Name"
                    val citizenNumber = edRegisterNik.text?.toString() ?: "0000000000000000"
                    val phone = edRegisterPhone.text?.toString() ?: "000000000000"
                    val address = edRegisterAddress.text?.toString() ?: "Default Address"
                    val memberType = selectedPackage

                    viewModel.completeUserData(
                        id = userId,
                        name = name,
                        pathKTP = idPicturePath,
                        citizenNumber = citizenNumber,
                        phone = phone,
                        address = address,
                        memberType = memberType
                    ).observe(this@HomeMemberRegisterActivity) { result ->
                        when (result) {
                            is Resource.Error -> {
                                showLoading(false)
                                isButtonEnabled(true)

                                if (!isInternetAvailable(this@HomeMemberRegisterActivity)) {
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

                                // Debug memberType
                                Log.d("Membertype Update", "Membertype updated to $memberType")

                                // Status Template Activity
                                val statusTemplate = StatusTemplate(
                                    title = "Membership Berhasil",
                                    description = "Membership berhasil Membership berhasil Membership berhasil Membership berhasil Membership berhasil Membership berhasil Membership berhasil Membership berhasil ",
                                    showCoupon = false,
                                    promoCode = "",
                                    expiryTime = "",
                                    buttonText = "Selesai"
                                )
                                val intent = Intent(this@HomeMemberRegisterActivity, StatusTemplateActivity::class.java).apply {
                                    putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
                                }
                                startActivity(intent)
                                finish()

                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val SELECTED_PACKAGE = "selected_package"
    }

}