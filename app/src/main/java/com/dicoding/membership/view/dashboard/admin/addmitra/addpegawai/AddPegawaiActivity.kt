package com.dicoding.membership.view.dashboard.admin.addmitra.addpegawai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantRequest
import com.dicoding.core.data.source.remote.response.merchants.MerchantData
import com.dicoding.core.data.source.remote.response.merchants.UserData
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityAddPegawaiBinding
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPegawaiActivity : AppCompatActivity() {
    private var _binding: ActivityAddPegawaiBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddPegawaiViewModel by viewModels()

    private var isEditMode = false
    private lateinit var merchantData: MerchantData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddPegawaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()

        setupMode()

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

    private fun setupMode() {
        isEditMode = intent.getBooleanExtra(EXTRA_IS_EDIT, false)
        merchantData = intent.getParcelableExtra(EXTRA_MERCHANT_DATA)!!

        binding.tvTitle.text = if (isEditMode) "Edit Pegawai" else "Tambah Pegawai"
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

            // Validasi form tidak boleh kosong
            if (ownerName.isEmpty()) addPegawaiNamaOwner.error = "Nama tidak boleh kosong"
            if (ownerEmail.isEmpty()) addPegawaiEmailOwner.error = "Email tidak boleh kosong"
            if (ownerTelepon.isEmpty()) addPegawaiTeleponOwner.error = "Nomor telepon tidak boleh kosong"
            if (ownerTelepon.length < 10 || ownerTelepon.length > 13) {
                addPegawaiTeleponOwner.error = "Nomor telepon harus 10-13 digit"
            }
            if (!ownerTelepon.matches(Regex("^[0-9]+$"))) {
                addPegawaiTeleponOwner.error = "Nomor telepon hanya boleh angka"
            }

            if (receptionistName.isEmpty()) addPegawaiNamaResepsionis.error = "Nama tidak boleh kosong"
            if (receptionistEmail.isEmpty()) addPegawaiEmailResepsionis.error = "Email tidak boleh kosong"
            if (receptionistTelepon.isEmpty()) addPegawaiTeleponResepsionis.error = "Nomor telepon tidak boleh kosong"
            if (receptionistTelepon.length < 10 || receptionistTelepon.length > 13) {
                addPegawaiTeleponResepsionis.error = "Nomor telepon harus 10-13 digit"
            }
            if (!receptionistTelepon.matches(Regex("^[0-9]+$"))) {
                addPegawaiTeleponResepsionis.error = "Nomor telepon hanya boleh angka"
            }

            // Validasi password match
            if (ownerPassword != ownerConfirmPassword) {
                addPegawaiKonfirmasiKataSandiOwner.error = "Password tidak sama"
            }
            if (receptionistPassword != receptionistConfirmPassword) {
                addPegawaiKonfirmasiKataSandiResepsionis.error = "Password tidak sama"
            }

            // Enable button jika semua validasi terpenuhi
            addMitraLanjutkanBtn.isEnabled = ownerName.isNotEmpty() &&
                    ownerEmail.isNotEmpty() &&
                    ownerTelepon.isNotEmpty() &&
                    ownerTelepon.length in 10..13 &&
                    ownerTelepon.matches(Regex("^[0-9]+$")) &&
                    ownerPassword.isNotEmpty() &&
                    ownerConfirmPassword.isNotEmpty() &&
                    ownerPassword == ownerConfirmPassword &&
                    receptionistName.isNotEmpty() &&
                    receptionistEmail.isNotEmpty() &&
                    receptionistTelepon.isNotEmpty() &&
                    receptionistTelepon.length in 10..13 &&
                    receptionistTelepon.matches(Regex("^[0-9]+$")) &&
                    receptionistPassword.isNotEmpty() &&
                    receptionistConfirmPassword.isNotEmpty() &&
                    receptionistPassword == receptionistConfirmPassword
        }
    }

    private fun setupSubmitButton() {
        binding.addMitraLanjutkanBtn.setOnClickListener {
            val request = createMerchantRequest()

            Log.d("AddPegawai", "Request: $request")

            if (isEditMode) {
                val merchantId = intent.getStringExtra(EXTRA_MERCHANT_ID)
                if (merchantId != null) {
                    viewModel.updateMerchant(
                        id = merchantId,
                        merchantData = request.merchantData,
                        ownerData = request.ownerData,
                        receptionistData = request.receptionistData
                    ).observe(this) { result ->
                        handleResource(result,
                            onSuccess = {
                                navigateToStatus(
                                    "Update Berhasil",
                                    "Data mitra dan pegawai berhasil diperbarui"
                                )
                            },
                            onMessage = {
                                showToast(it ?: "Informasi tidak tersedia")
                            }
                        )
                    }
                }
            } else {
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

    companion object {
        const val EXTRA_IS_EDIT = "extra_is_edit"
        const val EXTRA_MERCHANT_DATA = "extra_merchant_data"
        const val EXTRA_MERCHANT_ID = "extra_merchant_id"
    }
}