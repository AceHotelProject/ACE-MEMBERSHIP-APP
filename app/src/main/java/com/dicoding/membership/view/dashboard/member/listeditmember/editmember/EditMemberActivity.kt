package com.dicoding.membership.view.dashboard.member.listeditmember.editmember

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityDetailMemberBinding
import com.dicoding.membership.databinding.ActivityEditMemberBinding
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMemberBinding
    private val viewModel: EditMemberViewModel by viewModels()
    private var membershipId: String? = null
    private var screenTitle: String? = null
    private var selectedBigBannerUri: Uri? = null
    private var selectedSmallBannerUri: Uri? = null
    private var previousBigBannerUrl: String? = null
    private var previousSmallBannerUrl: String? = null
    private var isImageBigSelected = false
    private var isImageSmallSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Data passed: Title & button
        screenTitle = intent.getStringExtra("SCREEN_TITLE") ?: "Edit Membership"  // Default value if null
        val buttonText = intent.getStringExtra("BUTTON_TEXT") ?: "Simpan"  // Default value if null
        binding.detailTitle.text = screenTitle
        binding.btnAdd.text = buttonText

        // Data passed: membership ID
        membershipId = intent.getStringExtra("MEMBERSHIP_ID")

        setupUI()

        membershipId?.let { id ->
            viewModel.getMembershipById(id)
        }
    }

    private fun setupUI() {
        // Basic UI setup
        binding.detailTitle.text = screenTitle
        binding.btnAdd.text = if (membershipId.isNullOrEmpty()) "Tambah" else "Simpan"
        isButtonEnabled(false)

        // If membershipId exists, fetch and display membership data
        membershipId?.let { id ->
            showLoading(true)
            viewModel.getMembershipById(id)
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.membershipData.collect { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                showLoading(true)
                            }
                            is Resource.Success -> {
                                showLoading(false)
                                resource.data?.let { membership ->
                                    // Prefill form fields
                                    binding.apply {
                                        tiTipeMember.setText(membership.type)
                                        tiTipeHari.setText(membership.duration.toString())
                                        tiTipeHarga.setText(membership.price.toString())
                                        tiMaxCoupon.setText(membership.maxCoupon.toString())

                                        // Handle images
                                        if (membership.image.isNotEmpty()) {
                                            previousBigBannerUrl = membership.image.getOrNull(0)
                                            previousSmallBannerUrl = membership.image.getOrNull(1)

                                            previousBigBannerUrl?.let { url ->
                                                Glide.with(this@EditMemberActivity)
                                                    .load(url)
                                                    .centerCrop()
                                                    .into(addFotoBannerBig)
                                            }

                                            previousSmallBannerUrl?.let { url ->
                                                Glide.with(this@EditMemberActivity)
                                                    .load(url)
                                                    .centerCrop()
                                                    .into(addFotoBannerSmall)
                                            }
                                        }

                                        // Convert TNC list to semicolon-separated string
                                        tiTipeSyaratKetentuan.setText(membership.tnc.joinToString("; "))
                                    }
                                }
                            }
                            is Resource.Error -> {
                                showLoading(false)
                                showError(resource.message ?: "Failed to load membership data")
                            }
                            else -> {
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        }

        // Setup other UI components
        setupBackButton()
        setupImagePickers()
        handleEditText()
        setupSubmitButton()
        observeMembershipState()
    }

    private fun setupBackButton() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupSubmitButton() {
        binding.btnAdd.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private val bigBannerGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            selectedBigBannerUri = selectedUri
            isImageBigSelected = true

            // Show image locally
            Glide.with(this)
                .load(selectedUri)
                .centerCrop()
                .into(binding.addFotoBannerBig)
        }
    }

    private val smallBannerGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            selectedSmallBannerUri = selectedUri
            isImageSmallSelected = true

            // Show image locally
            Glide.with(this)
                .load(selectedUri)
                .centerCrop()
                .into(binding.addFotoBannerSmall)
        }
    }

    // Modify setupImagePickers to use GetContent
    private fun setupImagePickers() {
        binding.addFotoBannerBig.setOnClickListener {
            bigBannerGalleryLauncher.launch("image/*")
        }

        binding.addFotoBannerSmall.setOnClickListener {
            smallBannerGalleryLauncher.launch("image/*")
        }
    }

    private fun openGallery(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private fun showConfirmationDialog() {
        val dialog = GlobalTwoButtonDialog().apply {
            setDialogTitle("Konfirmasi Data")
            setDialogMessage("Apakah anda sudah yakin dengan data yang sudah anda isi?")
            setOnYesClickListener {
                showLoading(true)
                submitData()
            }
            setOnNoClickListener {
                // Dialog will automatically dismiss
            }
        }
        dialog.show(supportFragmentManager, "confirmation_dialog")
    }

    private fun submitData() {
        // Get existing form data
        val type = binding.tiTipeMember.text.toString()
        val duration = binding.tiTipeHari.text.toString().toInt()
        val maxCoupon = binding.tiMaxCoupon.text.toString().toInt()
        val price = binding.tiTipeHarga.text.toString().toInt()
        val tncString = binding.tiTipeSyaratKetentuan.text.toString()
        val tncList = if (tncString.contains(";")) {
            tncString.split(";").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            listOf(tncString.trim())
        }

        lifecycleScope.launch {
            val imageUrls = mutableListOf<String>()

            // Handle big banner
            if (isImageBigSelected && selectedBigBannerUri != null) {
                // Delete old image if exists
                previousBigBannerUrl?.let { oldUrl ->
                    try {
                        viewModel.deleteFile(oldUrl).collect { result ->
                            when (result) {
                                is Resource.Success -> Log.d("ImageDelete", "Successfully deleted old big banner")
                                is Resource.Error -> Log.e("ImageDelete", "Failed to delete old big banner: ${result.message}")
                                else -> {}
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ImageDelete", "Error deleting big banner: ${e.message}")
                    }
                }

                // Upload new image
                viewModel.uploadFile(selectedBigBannerUri!!, this@EditMemberActivity)
                    .collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let { upload ->
                                    imageUrls.add(upload.fileUrl)
                                }
                            }
                            is Resource.Error -> {
                                showToast("Failed to upload big banner: ${result.message}")
                                return@collect
                            }
                            else -> {}
                        }
                    }
            } else {
                imageUrls.add(previousBigBannerUrl ?: "")
            }

            // Handle small banner
            if (isImageSmallSelected && selectedSmallBannerUri != null) {
                // Delete old image if exists
                previousSmallBannerUrl?.let { oldUrl ->
                    try {
                        viewModel.deleteFile(oldUrl).collect { result ->
                            when (result) {
                                is Resource.Success -> Log.d("ImageDelete", "Successfully deleted old small banner")
                                is Resource.Error -> Log.e("ImageDelete", "Failed to delete old small banner: ${result.message}")
                                else -> {}
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ImageDelete", "Error deleting small banner: ${e.message}")
                    }
                }

                // Upload new image
                viewModel.uploadFile(selectedSmallBannerUri!!, this@EditMemberActivity)
                    .collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let { upload ->
                                    imageUrls.add(upload.fileUrl)
                                }
                            }
                            is Resource.Error -> {
                                showToast("Failed to upload small banner: ${result.message}")
                                return@collect
                            }
                            else -> {}
                        }
                    }
            } else {
                imageUrls.add(previousSmallBannerUrl ?: "")
            }

            // Submit membership data with image URLs
            if (binding.btnAdd.text == "Simpan") {
                membershipId?.let { id ->
                    viewModel.updateMembership(id, type, maxCoupon, duration, price, tncList, imageUrls)
                }
            } else {
                viewModel.createMembership(type, duration, maxCoupon, price, tncList, imageUrls)
            }
        }
    }

    private fun observeMembershipState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.membershipState.collect { resource ->
                    when (resource) {
                        null -> {
                            // Initial state, form is ready for input
                            binding.btnAdd.isEnabled = true
                            showLoading(false)
                        }
                        is Resource.Loading -> {
                            binding.btnAdd.isEnabled = false
                            Log.d("DebugMember", "Loading..")
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            var statusTemplate = StatusTemplate("", "", false, "", "", "")
                            if(screenTitle=="Tambah Membership"){
                                statusTemplate = StatusTemplate(
                                    title = "Berhasil dibuat",
                                    description = "Data membership telah berhasil dibuat",
                                    showCoupon = false,
                                    promoCode = "",
                                    expiryTime = "",
                                    buttonText = "Selesai"
                                )
                            } else {
                                statusTemplate = StatusTemplate(
                                    title = "Perbaharui Berhasil",
                                    description = "Data membership telah berhasil disimpan",
                                    showCoupon = false,
                                    promoCode = "",
                                    expiryTime = "",
                                    buttonText = "Selesai"
                                )
                            }


                            val intent = Intent(this@EditMemberActivity, StatusTemplateActivity::class.java).apply {
                                putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            }
                            startActivity(intent)
                            finish()
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            binding.btnAdd.isEnabled = true
                            showError(resource.message ?: "An error occurred")
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnAdd.isEnabled = isEnabled
    }

    private fun handleEditText() {
        binding.tiTipeMember.addTextChangedListener(object : TextWatcher {
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

        binding.tiTipeHari.addTextChangedListener(object : TextWatcher {
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

        binding.tiTipeHarga.addTextChangedListener(object : TextWatcher {
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

        binding.tiTipeSyaratKetentuan.addTextChangedListener(object : TextWatcher {
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

//        binding.tiTipePotonganHarga.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                checkForms()
//            }
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                checkForms()
//            }
//            override fun afterTextChanged(p0: Editable?) {
//                checkForms()
//            }
//        })
    }

    private fun checkForms() {
        binding.apply {
            val tipemember = tiTipeMember.text.toString()
            val Hariberlaku = tiTipeHari.text.toString()
            val hargamember = tiTipeHarga.text.toString()
            val syaratketentuan = tiTipeSyaratKetentuan.text.toString()
            //val potonganharga = tiTipePotonganHarga.text.toString()

            // Validasi Tipe Member
            if (tipemember.isEmpty()) {
                binding.tiTipeMember.error = "Tipe member tidak boleh kosong"
            } else if (tipemember.length > 20) {
                binding.tiTipeMember.error = "Tipe member maksimal 20 karakter"
            } else if (!tipemember.matches(Regex("^[a-zA-Z ]+$"))) {
                binding.tiTipeMember.error = "Tipe member hanya boleh huruf"
            } else {
                binding.tiTipeMember.error = null
            }

            // Validasi Hari Berlaku
            if (Hariberlaku.isEmpty()) {
                binding.tiTipeHari.error = "Hari berlaku tidak boleh kosong"
            } else {
                try {
                    val HariValue = Hariberlaku.toInt()
                    if (HariValue <= 0) {
                        binding.tiTipeHari.error = "Hari berlaku tidak boleh 0 atau negatif"
                    } else {
                        binding.tiTipeHari.error = null
                    }
                } catch (e: NumberFormatException) {
                    binding.tiTipeHari.error = "Hari berlaku harus berupa angka"
                }
            }

            // Validasi Harga Member
            if (hargamember.isEmpty()) {
                binding.tiTipeHarga.error = "Harga member tidak boleh kosong"
            } else {
                try {
                    val hargaValue = hargamember.toInt()
                    if (hargaValue <= 0) {
                        binding.tiTipeHarga.error = "Harga member tidak boleh 0 atau negatif"
                    } else if (hargaValue > 50000000) {
                        binding.tiTipeHarga.error = "Harga member maksimal 50.000.000"
                    } else {
                        binding.tiTipeHarga.error = null
                    }
                } catch (e: NumberFormatException) {
                    binding.tiTipeHarga.error = "Harga member harus berupa angka"
                }
            }

            // Validasi Syarat dan Ketentuan
            if (syaratketentuan.isEmpty()) {
                binding.tiTipeSyaratKetentuan.error = "Syarat dan ketentuan tidak boleh kosong"
            } else if (syaratketentuan.length > 200) {
                binding.tiTipeSyaratKetentuan.error = "Syarat dan ketentuan maksimal 200 karakter"
            } else {
                binding.tiTipeSyaratKetentuan.error = null
            }

            // Validasi Potongan Harga
//            if (potonganharga.isEmpty()) {
//                binding.tiTipePotonganHarga.error = "Potongan harga tidak boleh kosong"
//            } else {
//                try {
//                    val potonganValue = potonganharga.toInt()
//                    if (potonganValue <= 0) {
//                        binding.tiTipePotonganHarga.error = "Potongan harga tidak boleh 0 atau negatif"
//                    } else if (potonganValue > 1000000) {
//                        binding.tiTipePotonganHarga.error = "Potongan harga maksimal 1.000.000"
//                    } else {
//                        binding.tiTipePotonganHarga.error = null
//                    }
//                } catch (e: NumberFormatException) {
//                    binding.tiTipePotonganHarga.error = "Potongan harga harus berupa angka"
//                }
//            }

            // Enable button jika semua validasi terpenuhi
            isButtonEnabled(
                tipemember.isNotEmpty() && tipemember.length <= 20 && tipemember.matches(Regex("^[a-zA-Z ]+$")) &&
                        Hariberlaku.isNotEmpty() && try { Hariberlaku.toInt() > 0} catch (e: NumberFormatException) { false } &&
                        hargamember.isNotEmpty() && try { hargamember.toInt() in 1..50000000 } catch (e: NumberFormatException) { false } &&
                        syaratketentuan.isNotEmpty() && syaratketentuan.length <= 200
                        //potonganharga.isNotEmpty() && try { potonganharga.toInt() in 1..1000000 } catch (e: NumberFormatException) { false }
            )
        }
    }

    private fun showLoading(boolean: Boolean) {
        binding.loadingOverlay.visibility = if(boolean) View.VISIBLE else View.GONE
        binding.btnClose.visibility = if(boolean) View.GONE else View.VISIBLE
        binding.detailTitle.visibility = if(boolean) View.GONE else View.VISIBLE
        binding.mainSv.visibility = if(boolean) View.GONE else View.VISIBLE

    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}