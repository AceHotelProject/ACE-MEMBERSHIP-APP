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
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityDetailValidasiBinding
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
    private var currentPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                viewModel.selectedImageUri = uri
                binding.ivBuktipembayaran.setImageURI(uri)
            }
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

        // Get user ID from intent and fetch data
        intent.getStringExtra(EXTRA_USER_ID)?.let { userId ->
            viewModel.getUserData(userId)
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
                    // Show loading if needed
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
                    showLoading(false)
                    Toast.makeText(this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show()
                    finish()
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
                labelMembershipType.text = membership.subscriptionType.type
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

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.ivBuktipembayaran.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.btnSimpan.setOnClickListener {
            viewModel.selectedImageUri?.let { uri ->
                showLoading(true)
                viewModel.uploadFile(uri, this)
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

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        private const val IMAGE_PICK_CODE = 1000
    }

}