package com.dicoding.membership.view.dashboard.home.member.mlevel

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityHomeMemberLevelBinding
import com.dicoding.membership.view.dashboard.home.member.mreferral.HomeMemberReferralActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class HomeMemberLevelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeMemberLevelBinding
    private var selectedPackage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }
    private fun setupClickListeners() {
        // Close button listener
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Package selection listeners
        binding.layoutPackageSilver.setOnClickListener {
            updatePackageSelection("silver")
        }

        binding.layoutPackageGold.setOnClickListener {
            updatePackageSelection("gold")
        }

        binding.layoutPackagePlatinum.setOnClickListener {
            updatePackageSelection("platinum")
        }

        // Continue button listener
        binding.btnContinue.setOnClickListener {
            if (selectedPackage != null) {
                // Navigate to referral activity
                startActivity(Intent(this, HomeMemberReferralActivity::class.java).apply {
                    // Optional: Pass selected package information
                    putExtra("SELECTED_PACKAGE", selectedPackage)
                })
            }
        }
    }

    private fun updatePackageSelection(selectedPackageTemp: String) {

        val normalTextColor = ContextCompat.getColor(this, R.color.black) // or your normal text color
        val selectedTextColor = ContextCompat.getColor(this, R.color.orange_100) // or your selected text color


        // Reset all backgrounds to non-selected
        binding.layoutPackageSilver.setBackgroundResource(R.drawable.custom_background_level_member_non_selected)
        binding.layoutPackageGold.setBackgroundResource(R.drawable.custom_background_level_member_non_selected)
        binding.layoutPackagePlatinum.setBackgroundResource(R.drawable.custom_background_level_member_non_selected)

        // Update price section styles to non-selected
        binding.priceContainerSilver.setBackgroundResource(R.drawable.custom_background_light_grey)
        binding.priceContainerGold.setBackgroundResource(R.drawable.custom_background_light_grey)
        binding.priceContainerPlatinum.setBackgroundResource(R.drawable.custom_background_light_grey)

        // Update text
        binding.tvPackagePriceLabel1.setTextColor(normalTextColor)
        binding.tvPackagePrice1.setTextColor(normalTextColor)
        binding.tvPackagePriceLabel2.setTextColor(normalTextColor)
        binding.tvPackagePrice2.setTextColor(normalTextColor)
        binding.tvPackagePriceLabel3.setTextColor(normalTextColor)
        binding.tvPackagePrice3.setTextColor(normalTextColor)

        // Set selected package background and styles
        when (selectedPackageTemp) {
            "silver" -> {
                binding.layoutPackageSilver.setBackgroundResource(R.drawable.custom_background_level_member_selected)
                binding.priceContainerSilver.setBackgroundResource(R.drawable.custom_background_level_member_price_selected)
                binding.tvPackagePriceLabel1.setTextColor(selectedTextColor)
                binding.tvPackagePrice1.setTextColor(selectedTextColor)
            }
            "gold" -> {
                binding.layoutPackageGold.setBackgroundResource(R.drawable.custom_background_level_member_selected)
                binding.priceContainerGold.setBackgroundResource(R.drawable.custom_background_level_member_price_selected)
                binding.tvPackagePriceLabel2.setTextColor(selectedTextColor)
                binding.tvPackagePrice2.setTextColor(selectedTextColor)
            }
            "platinum" -> {
                binding.layoutPackagePlatinum.setBackgroundResource(R.drawable.custom_background_level_member_selected)
                binding.priceContainerPlatinum.setBackgroundResource(R.drawable.custom_background_level_member_price_selected)
                binding.tvPackagePriceLabel3.setTextColor(selectedTextColor)
                binding.tvPackagePrice3.setTextColor(selectedTextColor)
            }
        }

        selectedPackage = selectedPackageTemp
    }
    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}