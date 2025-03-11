package com.dicoding.membership.view.dashboard.floatingvalidasi.detailvalidasi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityDetailValidasiBinding
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class DetailValidasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailValidasiBinding
    private val viewModel: DetailValidasiViewModel by viewModels()
    private var userId: String? = null
    private var done: Boolean = false

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.selectedImageUri = it
            binding.ivBuktipembayaran.setImageURI(it)
            updateButtonState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailValidasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)
        setupObserver()
        setupClickListeners()
        setupUploadObserver()
        setupVerifyObserver()
        updateButtonState()

        // Get user ID from intent and fetch data
        userId = intent.getStringExtra(EXTRA_USER_ID)
        userId?.let { id ->
            viewModel.getUserData(id)
        } ?: run {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupObserver() {
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { user ->
                        updateUserDataUI(user)
                        updateMembershipDataUI(user)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        resource.message ?: "Terjadi kesalahan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Message -> TODO()
            }
        }
    }

    private fun setupUploadObserver() {
        viewModel.uploadState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    // Don't hide loading here as we're proceeding to verify
                    // We'll get response from verifyUserState instead
                    resource.data?.let { fileUpload ->
                        // Now verify the user with uploaded proof URL
                        userId?.let { id ->
                            viewModel.verifyUser(id, fileUpload.fileUrl)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        resource.message ?: "Gagal mengupload gambar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Message -> TODO()
            }
        }
    }

    private fun setupVerifyObserver() {
        viewModel.verifyUserState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    if(!done){
                        resource.data?.let { user ->
                            navigateToSuccessScreen(user)
                        }
                        done = !done
                    }

                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        resource.message ?: "Gagal melakukan verifikasi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Message -> TODO()
            }
        }
    }

    private fun navigateToSuccessScreen(user: User) {
        val statusTemplate = StatusTemplate(
            title = "Validasi Berhasil",
            description = "Validasi terhadap user ${user.name} telah berhasil dilakukan!",
            showCoupon = false,
            buttonText = "Selesai"
        )

        val intent = Intent(this, StatusTemplateActivity::class.java).apply {
            putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
        }
        startActivity(intent)
        finish()
    }

    private fun updateUserDataUI(user: User) {
        with(binding) {
            tvName.text = user.name
            tvCitizenNumber.text = user.citizenNumber ?: "-"
            tvPhone.text = user.phone ?: "-"
            tvAddress.text = user.address ?: "-"
            tvEmail.text = user.email

            // Load KTP Image
            user.pathKTP?.let { ktpUrl ->
                Glide.with(this@DetailValidasiActivity)
                    .load(ktpUrl)
                    .placeholder(R.drawable.ktp_example)
                    .error(R.drawable.ktp_example)
                    .into(detailProfileKtpImageView)
            }
        }
    }

    private fun updateMembershipDataUI(user: User) {
        user.membership?.let { membership ->
            with(binding) {
                labelMembershipType.text = membership.subscriptionType?.type ?: "-"
                if (membership.status == "pending") {
                    labelStatus.background = ContextCompat.getDrawable(this@DetailValidasiActivity, R.drawable.chip_category_red)
                    labelStatus.setTextColor(ContextCompat.getColor(this@DetailValidasiActivity, R.color.red))
                } else {
                    labelStatus.background = ContextCompat.getDrawable(this@DetailValidasiActivity, R.drawable.chip_category_green)
                    labelStatus.setTextColor(ContextCompat.getColor(this@DetailValidasiActivity, R.color.green))
                }
                labelStatus.text = membership.status
                tvHarga.text = formatCurrency(membership.payment)
                tvExpMember.text = formatDateTime(membership.endDate)
                tvVerifikator.text = membership.verificatorId ?: "-"
                tvTglTransaksi.text = formatDateTime(membership.startDate)

                membership.paymentProof?.let { proofUrl ->
                    Glide.with(this@DetailValidasiActivity)
                        .load(proofUrl)
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(ivBuktipembayaran)
                }
            }
        }
    }

    private fun updateButtonState() {
        binding.btnSimpan.isEnabled = viewModel.selectedImageUri != null
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.ivBuktipembayaran.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnSimpan.setOnClickListener {
            viewModel.selectedImageUri?.let { uri ->
                // Show confirmation dialog before proceeding
                showConfirmationDialog(uri)
            } ?: run {
                Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(b: Boolean) {
        binding.apply{
            loadingOverlay.visibility = if (b) View.VISIBLE else View.GONE
            scrollableContent.visibility = if (!b) View.VISIBLE else View.GONE
        }
    }

    private fun formatDateTime(dateTimeString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat = SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale("id"))
            outputFormat.timeZone = TimeZone.getDefault()

            val date = inputFormat.parse(dateTimeString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "-"
        }
    }

    private fun formatCurrency(amount: Int): String {
        return try {
            val formattedNumber = String.format("%,d", amount).replace(',', '.')
            "Rp $formattedNumber"
        } catch (e: Exception) {
            "Rp 0"
        }
    }

    private fun showConfirmationDialog(uri: Uri) {
        val dialog = GlobalTwoButtonDialog().apply {
            setDialogTitle("Konfirmasi Membership")
            setDialogMessage("Apakah Anda yakin ingin memvalidasi user ini?")

            setOnYesClickListener {
                // User confirmed, proceed with upload and verification
                showLoading(true)
                userId?.let { id ->
                    viewModel.uploadProofAndVerifyUser(id, uri, this@DetailValidasiActivity)
                }
            }

            setOnNoClickListener {
                // User canceled, do nothing
            }
        }

        dialog.show(supportFragmentManager, "ConfirmationDialog")
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}