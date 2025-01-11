package com.dicoding.membership.view.dashboard.floatingcoupon.reedemcoupon

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityRedeemPromoCodeBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RedeemCouponCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRedeemPromoCodeBinding
    private val viewModel: RedeemCouponCodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedeemPromoCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()

        handleMenuButton()

        isButtonEnabled(false)

        handleEditText()

        setupSubmitButton()

    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty()) {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun setupSubmitButton() {
        binding.btnContinue.setOnClickListener {
            if (binding.btnContinue.isEnabled) {
                val promoCode = binding.edReferralCode.text.toString()
                showConfirmationDialog(promoCode)
            } else {
                showFormError()
            }
        }
    }

    private fun showConfirmationDialog(promoCode: String) {
        GlobalTwoButtonDialog().apply {
            setDialogTitle("Konfirmasi Redeem")
            setDialogMessage("Apakah Anda yakin ingin menggunakan kode promo ini?")
            setOnYesClickListener {
                handleRedeemPromo(promoCode)
            }
        }.show(supportFragmentManager, "ConfirmationDialog")
    }

    private fun handleRedeemPromo(promoCode: String) {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                showLoading()
                viewModel.redeemPromo(promoCode).observe(this) { result ->
                    when (result) {
                        is Resource.Success -> {
                            hideLoading()
                            navigateToStatus(
                                "Promo Berhasil Digunakan",
                                "Selamat! Promo telah berhasil digunakan"
                            )
                        }
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Error -> {
                            hideLoading()
                            showToast(result.message ?: "Terjadi kesalahan")
                        }
                        else -> {
                            hideLoading()
                            showToast("Terjadi kesalahan")
                        }
                    }
                }
            } else {
                showToast("Token tidak valid")
            }
        }
    }

    private fun showFormError() {
        binding.apply {
            val kodereferral = edReferralCode.text.toString()
            var message = "Mohon lengkapi:"

            if (kodereferral.isEmpty()) {
                message += "\n- Kode Promo"
            } else if (kodereferral.length > 8) {
                message += "\n- Kode promo maksimal 8 karakter"
            }

            if (message != "Mohon lengkapi:") {
                AlertDialog.Builder(this@RedeemCouponCodeActivity)
                    .setTitle("Peringatan")
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun handleMenuButton() {
        binding.btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnContinue.isEnabled = isEnabled
    }

    private fun handleEditText() {
        binding.edReferralCode.addTextChangedListener(object : TextWatcher {
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
            val kodereferral = edReferralCode.text.toString()

            // Validasi Kode Pengguna
            if (kodereferral.isEmpty()) {
                binding.edReferralCode.error = "Kode pengguna tidak boleh kosong"
            } else if (kodereferral.length > 8) {
                binding.edReferralCode.error = "Kode pengguna maksimal 8 karakter"
            } else {
                binding.edReferralCode.error = null
            }

            // Enable button jika semua validasi terpenuhi
            isButtonEnabled(
                kodereferral.isNotEmpty() && kodereferral.length <= 8
            )
        }
    }

    private fun navigateToStatus(
        title: String,
        description: String
    ) {
        val statusTemplate = StatusTemplate(
            title = title,
            description = description,
            showCoupon = false,
            buttonText = "Selesai"
        )

        val intent = Intent(this, StatusTemplateActivity::class.java).apply {
            putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        binding.apply {
            btnContinue.isEnabled = false
            progressBar.visibility = View.VISIBLE
            loadingOverlay.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            btnContinue.isEnabled = true
            progressBar.visibility = View.GONE
            loadingOverlay.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}