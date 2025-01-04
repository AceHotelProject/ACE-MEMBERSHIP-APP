package com.dicoding.membership.view.status

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.databinding.ActivityStatusTemplateBinding
import com.dicoding.membership.view.dashboard.MainActivity

class StatusTemplateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatusTemplateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data dari intent
        val statusTemplate = intent.getParcelableExtra<StatusTemplate>(EXTRA_STATUS_TEMPLATE)

        setupUI(statusTemplate)
        setupButton()
    }

    private fun setupUI(statusTemplate: StatusTemplate?) {
        statusTemplate?.let { template ->
            binding.apply {
                // Set title dan description
                tvStatusTitle.text = template.title
                tvStatusDescription.text = template.description

                // Set visibility coupon layout
                layoutCoupon.visibility = if (template.showCoupon) View.VISIBLE else View.GONE

                // Set promo code dan expiry jika visible
                if (template.showCoupon) {
                    tvPromoCode.text = template.promoCode
                    tvExpiryTime.text = template.expiryTime
                }

                // Set button text
                btnSelesai.text = template.buttonText
            }
        }
    }

    private fun setupButton() {
        binding.btnSelesai.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_STATUS_TEMPLATE = "extra_status_template"
    }
}