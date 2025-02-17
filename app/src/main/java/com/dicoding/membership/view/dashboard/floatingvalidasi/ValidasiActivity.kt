package com.dicoding.membership.view.dashboard.floatingvalidasi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityRedeemPromoCodeBinding
import com.dicoding.membership.databinding.ActivityRegisterBinding
import com.dicoding.membership.databinding.ActivityValidasiBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.dashboard.profile.detail.poinku.transfer.TransferPoinActivity.Companion.EXTRA_USER_ID
import dagger.hilt.android.AndroidEntryPoint

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

        isButtonEnabled(false)
        handleEditText()
        handleMenuButton()
        setupImagePicker()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoadingDataPengguna(true)  // Show loading only for data pengguna section
                }
                is Resource.Success -> {
                    showLoadingDataPengguna(false)
                    resource.data?.let { user ->
                        if (user.id == intent.getStringExtra(EXTRA_USER_ID)) {
                            Toast.makeText(this, "Tidak dapat transfer ke nomor sendiri", Toast.LENGTH_SHORT).show()
                            clearUserDataUI()
                            isButton(false)
                        } else {
                            updateUserDataUI(user)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoadingDataPengguna(false)
                    // Show error message
                    Toast.makeText(
                        this,
                        resource.message ?: "Terjadi kesalahan",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Clear the UI data on error
                    clearUserDataUI()
                }
                is Resource.Message -> TODO()
            }
        }
    }

    private fun updateUserDataUI(user: User) {
        with(binding){
            tvMail.text = user.email
            tvTelepon.text = user.phone
            selectedUserId = user.id
        }
    }

    private fun isButton(b: Boolean){
        binding.btnContinue.isEnabled = b
    }

    private fun clearUserDataUI() {
        with(binding){
            tvMail.text = "Empty"
            tvTelepon.text = "Empty"
            selectedUserId = null
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnContinue.isEnabled = isEnabled

        if (!isEnabled && !isImageSelected) {
            binding.btnContinue.setOnClickListener {
                Toast.makeText(this, "Bukti pembayaran harus diupload", Toast.LENGTH_SHORT).show()
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
            val kodePengguna = binding.masukkanKodePenggunaVal.text.toString()

            if(kodePengguna.isBlank()) {
                binding.masukkanKodePengguna.error = "Kode pengguna tidak boleh kosong"
                return@setOnClickListener
            }
            viewModel.getUserDataByPhone(kodePengguna)

        }

    }

    private fun setupImagePicker() {
        binding.ivBuktipembayaran.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    private fun checkForms() {
        binding.apply {
            val kodepengguna = masukkanKodePenggunaVal.text.toString()

            // Validasi Kode Pengguna
            if (kodepengguna.isEmpty()) {
                binding.masukkanKodePenggunaVal.error = "Kode pengguna tidak boleh kosong"
            } else {
                binding.masukkanKodePenggunaVal.error = null
            }

            // Enable button jika semua validasi terpenuhi
            isButtonEnabled(
                kodepengguna.isNotEmpty() &&
                        kodepengguna.length <= 8 &&
                        isImageSelected
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

    private fun showLoadingDataPengguna(isLoading: Boolean){
        binding.loadingOverlayDataPengguna.visibility = if(isLoading) View.VISIBLE else View.GONE
        binding.dataPenggunaLayout.visibility = if(!isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

}