package com.dicoding.membership.view.dashboard.floatingcoupon.reedemcoupon

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.databinding.ActivityRedeemPromoCodeBinding
import com.dicoding.membership.view.dashboard.MainActivity

class RedeemCouponCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRedeemPromoCodeBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedeemPromoCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleMenuButton()
        isButtonEnabled(false)
        handleEditText()
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
}