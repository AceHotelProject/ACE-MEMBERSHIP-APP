package com.dicoding.membership.view.template

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityStatusTemplateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatusTemplateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatusTemplateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //functions implementation
        setupViews()
        setupButtons()
    }

    private fun setupViews() {
        // Get extras from intent
        intent.extras?.let { bundle ->
            // Set title and description (required fields)
            binding.tvStatusTitle.text = bundle.getString(EXTRA_TITLE, "")
            binding.tvStatusDescription.text = bundle.getString(EXTRA_DESCRIPTION, "")

            // Handle coupon visibility and data
            val showCoupon = bundle.getBoolean(EXTRA_SHOW_COUPON, false)
            if (showCoupon) {
                binding.apply {
                    // Show coupon layout
                    val couponCode = bundle.getString(EXTRA_COUPON_CODE, "")
                    val couponExpiry = bundle.getString(EXTRA_COUPON_EXPIRY, "")

                    if (couponCode.isNotEmpty() && couponExpiry.isNotEmpty()) {
                        tvPromoCode.text = couponCode
                        tvExpiryTime.text = "EXP $couponExpiry"
                    } else {
                        // Hide coupon layout if code or expiry is missing
                        //binding.root.findViewById<View>(R.id.coupon_layout).visibility = View.GONE
                    }
                }
            } else {
                // Hide coupon layout if not needed
                //binding.root.findViewById<View>(R.id.coupon_layout).visibility = View.GONE
            }
        }
    }

    private fun setupButtons() {
        binding.btnSelesai.setOnClickListener {
            //handle selesai
        }
    }

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_DESCRIPTION = "desc"
        const val EXTRA_SHOW_COUPON = "coupon"
        const val EXTRA_COUPON_CODE = "coupon_code"
        const val EXTRA_COUPON_EXPIRY = "coupon_expiry"
    }
}