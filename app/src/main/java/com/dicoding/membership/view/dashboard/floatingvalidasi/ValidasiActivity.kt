package com.dicoding.membership.view.dashboard.floatingvalidasi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityValidasiBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.dashboard.floatingvalidasi.detailvalidasi.DetailValidasiActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class ValidasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityValidasiBinding
    private val viewModel: ValidasiViewModel by viewModels()
    private var isImageSelected = false
    private var selectedUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isButton(false)
        handleEditText()
        handleMenuButton()
        setupImagePicker()
        setupObserver()
        setupVerificatorObserver()
    }

    private fun setupVerificatorObserver() {
        viewModel.userDataAdmin.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Already showing "Loading..." in the TextView
                }
                is Resource.Success -> {
                    resource.data?.let { admin ->
                        binding.tvVerifikator.text = admin.email ?: "Tidak diketahui"
                    }
                }
                is Resource.Error -> {
                    binding.tvVerifikator.text = "Tidak dapat memuat data verifikator"
                }
                is Resource.Message -> TODO()
            }
        }
    }

    private fun setupObserver() {
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoadingDataPengguna(true)
                }
                is Resource.Success -> {
                    Log.d("debug","Not found!")
                    showLoadingDataPengguna(false)
                    resource.data?.let { user ->
                        if (user.id == intent.getStringExtra(EXTRA_USER_ID)) {
                            Toast.makeText(this, "Tidak dapat transfer ke nomor sendiri", Toast.LENGTH_SHORT).show()
                            clearUserDataUI()
                            clearMembershipDataUI()
                            isButton(false)
                        } else {
                            updateUserDataUI(user)
                            updateMembershipDataUI(user)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoadingDataPengguna(false)
                    Toast.makeText(
                        this,
                        resource.message ?: "Terjadi kesalahan",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("debug","Not found?")
                    clearUserDataUI(true)
                    clearMembershipDataUI()
                }
                is Resource.Message -> TODO()
            }
        }
    }

    private fun updateUserDataUI(user: User) {
        with(binding) {
            tvMail.text = user.email
            tvTelepon.visibility = View.VISIBLE
            tvTelepon.text = user.phone
            selectedUserId = user.id
            isButton(user.membership?.status == "pending")
        }
    }

    private fun updateMembershipDataUI(user: User) {
        user.membership?.let { membership ->
            with(binding) {
                // Update subscription type
                labelMembershipType.text = membership.subscriptionType.type

                if (membership.status == "pending") {
                    labelStatus.background = ContextCompat.getDrawable(this@ValidasiActivity, R.drawable.chip_category_red)
                    labelStatus.setTextColor(ContextCompat.getColor(this@ValidasiActivity, R.color.red))
                } else {
                    labelStatus.background = ContextCompat.getDrawable(this@ValidasiActivity, R.drawable.chip_category_green)
                    labelStatus.setTextColor(ContextCompat.getColor(this@ValidasiActivity, R.color.green))
                }

                // Update status
                labelStatus.text = membership.status

                // Update payment
                tvHarga.text = formatCurrency(membership.payment)

                // Update end date with formatted date
                tvExpMember.text = formatDateTime(membership.endDate)

                // Update verificator ID
                if(membership.status == "pending"){
                    tvVerifikator.text = "Belum di Verifikasi"
                } else if(membership.status == "active") {
                    // If verificator ID exists, fetch verificator name
                    membership.verificatorId?.let { verificatorId ->
                        tvVerifikator.text = "Loading..."
                        viewModel.getUserDataAdmin(verificatorId)
                    } ?: run {
                        tvVerifikator.text = "-"
                    }
                }


                // Update transaction date with formatted date
                tvTglTransaksi.text = formatDateTime(membership.startDate)

                // Update payment proof image if available
                membership.paymentProof?.let { proofUrl ->
                    Glide.with(this@ValidasiActivity)
                        .load(proofUrl)
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(ivBuktipembayaran)
                }
            }
        }
    }

    private fun clearMembershipDataUI() {
        with(binding) {
            labelMembershipType.text = "-"
            labelStatus.text = "-"
            tvHarga.text = formatCurrency(0)
            tvExpMember.text = "-"
            tvVerifikator.text = "-"
            tvTglTransaksi.text = "-"
            ivBuktipembayaran.setImageResource(R.drawable.image_empty)
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

    private fun isButton(b: Boolean) {
        binding.btnContinue.isEnabled = b
    }

    private fun clearUserDataUI(notFound: Boolean = false) {
        with(binding) {
            if(notFound){
                tvMail.text = "User not found"
                tvTelepon.visibility = View.GONE
                selectedUserId = null
            } else {
                tvMail.text = "Empty"
                tvTelepon.visibility = View.VISIBLE
                tvTelepon.text = "Empty"
                selectedUserId = null
            }

        }
    }

    private fun handleEditText() {
        binding.masukkanKodePenggunaVal.addTextChangedListener(object : TextWatcher {
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

    private fun handleMenuButton() {
        binding.btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        binding.buttonCek.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.masukkanKodePenggunaVal.windowToken, 0)

            val kodePengguna = binding.masukkanKodePenggunaVal.text.toString()

            if(kodePengguna.isBlank()) {
                binding.masukkanKodePengguna.error = "Kode pengguna tidak boleh kosong"
                return@setOnClickListener
            }
            viewModel.getUserDataByPhone(kodePengguna)
        }
        binding.btnContinue.setOnClickListener {
            // Check if we have a selected user ID
            selectedUserId?.let { userId ->
                val intent = Intent(this, DetailValidasiActivity::class.java).apply {
                    putExtra(DetailValidasiActivity.EXTRA_USER_ID, userId)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupImagePicker() {
//        binding.ivBuktipembayaran.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent, IMAGE_PICK_CODE)
//        }
    }

    private fun checkForms() {
        binding.apply {
            val kodepengguna = masukkanKodePenggunaVal.text.toString()

            if (kodepengguna.isEmpty()) {
                masukkanKodePenggunaVal.error = "Kode pengguna tidak boleh kosong"
            } else {
                masukkanKodePenggunaVal.error = null
            }

            isButton(
                kodepengguna.isNotEmpty()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                binding.ivBuktipembayaran.setImageURI(uri)
                isImageSelected = true
                checkForms()
            }
        }
    }

    private fun showLoadingDataPengguna(isLoading: Boolean) {
        binding.loadingOverlayDataPengguna.visibility = if(isLoading) View.VISIBLE else View.GONE
        binding.layoutDataPengguna.visibility = if(!isLoading) View.VISIBLE else View.GONE
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
        private const val IMAGE_PICK_CODE = 1000
        const val EXTRA_USER_ID = "extra_user_id"
    }
}