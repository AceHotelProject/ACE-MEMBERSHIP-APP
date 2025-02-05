package com.dicoding.membership.view.dashboard.admin.addmitra.addpegawai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantRequest
import com.dicoding.core.data.source.remote.response.merchants.MerchantData
import com.dicoding.core.data.source.remote.response.merchants.UserData
import com.dicoding.membership.databinding.ActivityAddPegawaiBinding
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPegawaiActivity : AppCompatActivity() {
    private var _binding: ActivityAddPegawaiBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddPegawaiViewModel by viewModels()

    private lateinit var merchantData: MerchantData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddPegawaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()

        setupInitialData()

        handleEditText()

        setupSubmitButton()
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun setupInitialData() {
        merchantData = intent.getParcelableExtra(EXTRA_MERCHANT_DATA)!!
        binding.tvTitle.text = "Tambah Pegawai"
    }

    private fun handleEditText() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        }

        with(binding) {
            addPegawaiNamaOwner.addTextChangedListener(textWatcher)
            addPegawaiEmailOwner.addTextChangedListener(textWatcher)
            addPegawaiTeleponOwner.addTextChangedListener(textWatcher)
            addPegawaiKataSandiOwner.addTextChangedListener(textWatcher)
            addPegawaiKonfirmasiKataSandiOwner.addTextChangedListener(textWatcher)
            addPegawaiNamaResepsionis.addTextChangedListener(textWatcher)
            addPegawaiEmailResepsionis.addTextChangedListener(textWatcher)
            addPegawaiTeleponResepsionis.addTextChangedListener(textWatcher)
            addPegawaiKataSandiResepsionis.addTextChangedListener(textWatcher)
            addPegawaiKonfirmasiKataSandiResepsionis.addTextChangedListener(textWatcher)
        }
    }

    private fun checkForms() {
        with(binding) {
            val ownerName = addPegawaiNamaOwner.text.toString()
            val ownerEmail = addPegawaiEmailOwner.text.toString()
            val ownerTelepon = addPegawaiTeleponOwner.text.toString()
            val ownerPassword = addPegawaiKataSandiOwner.text.toString()
            val ownerConfirmPassword = addPegawaiKonfirmasiKataSandiOwner.text.toString()

            val receptionistName = addPegawaiNamaResepsionis.text.toString()
            val receptionistEmail = addPegawaiEmailResepsionis.text.toString()
            val receptionistTelepon = addPegawaiTeleponResepsionis.text.toString()
            val receptionistPassword = addPegawaiKataSandiResepsionis.text.toString()
            val receptionistConfirmPassword = addPegawaiKonfirmasiKataSandiResepsionis.text.toString()

            // Owner validations
            if (ownerName.isEmpty()) {
                addPegawaiNamaOwner.error = "Nama tidak boleh kosong"
            }

            // Email validation for owner
            if (!Patterns.EMAIL_ADDRESS.matcher(ownerEmail).matches()) {
                addPegawaiEmailOwner.error = "Format email tidak valid"
            }

            // Phone validation for owner
            if (ownerTelepon.isEmpty()) {
                addPegawaiTeleponOwner.error = "Nomor telepon tidak boleh kosong"
            } else if (ownerTelepon.length < 10 || ownerTelepon.length > 13) {
                addPegawaiTeleponOwner.error = "Nomor telepon harus 10-13 digit"
            } else if (!ownerTelepon.matches(Regex("^[0-9]+$"))) {
                addPegawaiTeleponOwner.error = "Nomor telepon hanya boleh angka"
            }

            // Password validation for owner
            if (ownerPassword.length < 8) {
                addPegawaiKataSandiOwner.error = "Password minimal 8 karakter"
            }
            if (ownerConfirmPassword.isNotEmpty() && ownerPassword != ownerConfirmPassword) {
                addPegawaiKonfirmasiKataSandiOwner.error = "Password tidak sama"
            }

            // Receptionist validations
            if (receptionistName.isEmpty()) {
                addPegawaiNamaResepsionis.error = "Nama tidak boleh kosong"
            }

            // Email validation for receptionist
            if (!Patterns.EMAIL_ADDRESS.matcher(receptionistEmail).matches()) {
                addPegawaiEmailResepsionis.error = "Format email tidak valid"
            }

            // Phone validation for receptionist
            if (receptionistTelepon.isEmpty()) {
                addPegawaiTeleponResepsionis.error = "Nomor telepon tidak boleh kosong"
            } else if (receptionistTelepon.length < 10 || receptionistTelepon.length > 13) {
                addPegawaiTeleponResepsionis.error = "Nomor telepon harus 10-13 digit"
            } else if (!receptionistTelepon.matches(Regex("^[0-9]+$"))) {
                addPegawaiTeleponResepsionis.error = "Nomor telepon hanya boleh angka"
            }

            // Password validation for receptionist
            if (receptionistPassword.length < 8) {
                addPegawaiKataSandiResepsionis.error = "Password minimal 8 karakter"
            }
            if (receptionistConfirmPassword.isNotEmpty() && receptionistPassword != receptionistConfirmPassword) {
                addPegawaiKonfirmasiKataSandiResepsionis.error = "Password tidak sama"
            }

            // Enable button if all validations pass
            addMitraLanjutkanBtn.isEnabled =
                ownerName.isNotEmpty() &&
                        Patterns.EMAIL_ADDRESS.matcher(ownerEmail).matches() &&
                        ownerTelepon.isNotEmpty() &&
                        ownerTelepon.length in 10..13 &&
                        ownerTelepon.matches(Regex("^[0-9]+$")) &&
                        ownerPassword.length >= 8 &&
                        ownerConfirmPassword.isNotEmpty() &&
                        ownerPassword == ownerConfirmPassword &&
                        receptionistName.isNotEmpty() &&
                        Patterns.EMAIL_ADDRESS.matcher(receptionistEmail).matches() &&
                        receptionistTelepon.isNotEmpty() &&
                        receptionistTelepon.length in 10..13 &&
                        receptionistTelepon.matches(Regex("^[0-9]+$")) &&
                        receptionistPassword.length >= 8 &&
                        receptionistConfirmPassword.isNotEmpty() &&
                        receptionistPassword == receptionistConfirmPassword
        }
    }

    private fun setupSubmitButton() {
        binding.addMitraLanjutkanBtn.setOnClickListener {
            if (binding.addMitraLanjutkanBtn.isEnabled) {
                // Sembunyikan keyboard
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                currentFocus?.let { view ->
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                }

                val dialog = GlobalTwoButtonDialog().apply {
                    setDialogTitle("Konfirmasi Tambah Mitra")
                    setDialogMessage("Apakah Anda yakin ingin menambahkan mitra baru?")
                    setOnYesClickListener {
                        handleCreateMerchant()
                    }
                }
                dialog.show(supportFragmentManager, "CreateMerchantDialog")
            }
        }
    }

    private fun handleCreateMerchant() {
        val request = createMerchantRequest()

        Log.d("AddPegawai", "Request: $request")

        viewModel.createMerchant(
            merchantData = request.merchantData,
            ownerData = request.ownerData,
            receptionistData = request.receptionistData
        ).observe(this) { result ->
            handleResource(result,
                onSuccess = {
                    navigateToStatus(
                        "Tambah Berhasil",
                        "Mitra baru berhasil ditambahkan"
                    )
                },
                onMessage = {
                    showToast(it ?: "Informasi tidak tersedia")
                }
            )
        }
    }

    private fun <T> handleResource(
        result: Resource<T>,
        onSuccess: (T?) -> Unit,
        onMessage: (String?) -> Unit
    ) {
        when (result) {
            is Resource.Success -> {
                hideLoading()
                onSuccess(result.data)
            }
            is Resource.Error -> {
                hideLoading()
                showToast(result.message ?: "Terjadi kesalahan")
            }
            is Resource.Message -> {
                hideLoading()
                onMessage(result.message)
            }
            is Resource.Loading -> showLoading()
        }
    }

    private fun navigateToStatus(
        title: String,
        description: String,
    ) {

        val statusTemplate = StatusTemplate(
            title = title,
            description = description,
            showCoupon = false,
            buttonText = "Selesai",
            promoCode = "",
            expiryTime = ""
        )

        val intent = Intent(this, StatusTemplateActivity::class.java).apply {
            putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun createMerchantRequest(): CreateMerchantRequest {
        val ownerData = UserData(
            name = binding.addPegawaiNamaOwner.text.toString(),
            email = binding.addPegawaiEmailOwner.text.toString(),
            phone = binding.addPegawaiTeleponOwner.text.toString(),
            password = binding.addPegawaiKataSandiOwner.text.toString()
        )

        val receptionistData = UserData(
            name = binding.addPegawaiNamaResepsionis.text.toString(),
            email = binding.addPegawaiEmailResepsionis.text.toString(),
            phone = binding.addPegawaiTeleponResepsionis.text.toString(),
            password = binding.addPegawaiKataSandiResepsionis.text.toString()
        )

        return CreateMerchantRequest(
            merchantData = merchantData,
            ownerData = ownerData,
            receptionistData = receptionistData
        )
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.addMitraLanjutkanBtn.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.addMitraLanjutkanBtn.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IS_EDIT = "extra_is_edit"
        const val EXTRA_MERCHANT_DATA = "extra_merchant_data"
        const val EXTRA_MERCHANT_ID = "extra_merchant_id"
    }
}