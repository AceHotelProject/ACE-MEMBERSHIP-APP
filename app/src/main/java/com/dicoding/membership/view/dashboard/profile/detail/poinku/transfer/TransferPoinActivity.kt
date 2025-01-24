package com.dicoding.membership.view.dashboard.profile.detail.poinku.transfer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.databinding.ActivityTransferPoinBinding
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferPoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransferPoinBinding
    private val viewModel: TransferPoinViewModel by viewModels()
    private var selectedUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferPoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //user id passed from previous activity
        val points = intent.getStringExtra(EXTRA_POINTS) ?: "Empty"
        Log.d("Debug points transfer", "Points data received: ${points}")

        //functions
        setupClickListeners()
        setupObserver()
        updatePointsUI(points)
        isButton(false)

        setupFormValidation()
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.buttonCek.setOnClickListener {
            val kodePengguna = binding.transferMasukkanKodePenggunaVal.text.toString()

            if (kodePengguna.isBlank()) {
                binding.transferMasukkanKodePengguna.error = "Kode pengguna tidak boleh kosong"
                return@setOnClickListener
            }

            viewModel.getUserDataByPhone(kodePengguna)
        }

        binding.transferButtonKirim.setOnClickListener {
            showConfirmationDialog()
        }
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
                            validateAllFields()
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
        viewModel.transferResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    val statusTemplate = StatusTemplate(
                        title = "Transfer Berhasil",
                        description = "Transfer dari user telah berhasil dilakukan!",
                        showCoupon = false,
                        buttonText = "Selesai"
                    )
                    Intent(this, StatusTemplateActivity::class.java).apply {
                        putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
                        startActivity(this)
                    }
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Message -> {}
            }
        }
    }

    private fun setupFormValidation() {
        binding.transferMasukkanNominalVal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().all { it.isDigit() }) {
                    binding.transferMasukkanNominal.error = "Hanya angka yang diperbolehkan"
                    isButton(false)
                } else {
                    binding.transferMasukkanNominal.error = null
                    validateAllFields()
                }
            }
        })

        // Add TextWatcher for other fields
        binding.transferCatatanVal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateAllFields()
            }
        })
    }

    private fun validateAndSubmitTransfer() {
        val fromUserId = intent.getStringExtra(EXTRA_USER_ID) ?: return
        val amount = binding.transferMasukkanNominalVal.text.toString().toIntOrNull()
        val notes = binding.transferCatatanVal.text.toString()

        when {
            selectedUserId == null -> {
                showError("Silakan cek data pengguna terlebih dahulu")
                return
            }
            amount == null || amount <= 0 -> {
                binding.transferMasukkanNominal.error = "Masukkan nominal yang valid"
                return
            }
            notes.isBlank() -> {
                binding.transferCatatan.error = "Masukkan catatan transfer"
                return
            }
        }

        viewModel.submitTransfer(
            to = selectedUserId!!,
            from = fromUserId,
            amount = amount!!,
            notes = notes
        )
    }

    private fun validateAllFields() {
        val nominal = binding.transferMasukkanNominalVal.text.toString()
        val catatan = binding.transferCatatanVal.text.toString()

        isButton(
            selectedUserId != null &&
                    nominal.isNotEmpty() &&
                    nominal.all { it.isDigit() } &&
                    catatan.isNotEmpty() &&
                    selectedUserId != intent.getStringExtra(EXTRA_USER_ID)
        )
    }

    private fun clearUserDataUI() {
        with(binding) {
            transferPenggunaEmail.text = ""
            transferPenggunaNomorHp.text = ""
            selectedUserId = null
        }
    }

    private fun updateUserDataUI(user: User) {
        with(binding) {
            transferPenggunaEmail.text = user.email
            transferPenggunaNomorHp.text = user.phone
            selectedUserId = user.id
        }
    }

    private fun updatePointsUI(points: String){
        binding.transferJumlahPoinku.text = points
    }

    private fun showConfirmationDialog() {
        val dialog = GlobalTwoButtonDialog().apply {
            setDialogTitle("Konfirmasi Transfer")
            setDialogMessage("Apakah anda yakin ingin melakukan transfer?")
            setOnYesClickListener {
                validateAndSubmitTransfer()
            }
            setOnNoClickListener {
                // Optional: handle no click
            }
        }
        dialog.show(supportFragmentManager, "transfer_confirmation")
    }


    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnClose.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.layoutTransferSv.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.transferTitle.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showLoadingDataPengguna(isLoading: Boolean) {
        binding.loadingOverlayDataPengguna.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.layoutDataPengguna.visibility = if(isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isButton(boolean: Boolean) {
        binding.transferButtonKirim.isEnabled = boolean
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_POINTS = "extra_points"
    }
}