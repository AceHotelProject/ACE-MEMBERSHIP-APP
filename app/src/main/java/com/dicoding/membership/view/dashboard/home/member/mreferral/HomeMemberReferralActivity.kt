package com.dicoding.membership.view.dashboard.home.member.mreferral

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.membership.databinding.ActivityHomeMemberReferralBinding
import com.dicoding.membership.view.dashboard.home.member.mregister.HomeMemberRegisterActivity
import com.dicoding.membership.view.dashboard.profile.detail.membershipku.ProfileDetailMembershipkuViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeMemberReferralActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeMemberReferralBinding
    private val viewModel: HomeMemberReferralViewModel by viewModels()
    private var selectedPackage: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberReferralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedPackage = intent.getStringExtra(SELECTED_PACKAGE) ?: return
        setupClickListeners()
        observeReferralState()

        // Initially hide the loading overlay
        showLoading(false)
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Continue Button implementation
        binding.btnContinue.setOnClickListener {
            // Get the referral code from input field
            val referralCode = binding.edReferralCode.text.toString().trim()

            // Call the ViewModel function to create referral token
            viewModel.createReferralToken(referralCode)
        }
    }

    private fun observeReferralState() {
        lifecycleScope.launch {
            viewModel.referralState.collect { state ->
                when (state) {
                    is HomeMemberReferralViewModel.ReferralState.Idle -> {
                        // Initial state, do nothing
                    }
                    is HomeMemberReferralViewModel.ReferralState.Loading -> {
                        // Show loading overlay
                        showLoading(true)
                    }
                    is HomeMemberReferralViewModel.ReferralState.Success -> {
                        // Navigate to next activity with the selected package
                        navigateToRegisterActivity()
                    }
                    is HomeMemberReferralViewModel.ReferralState.Error -> {
                        // Hide loading overlay
                        showLoading(false)

                        // Show error message
                        binding.layoutReferralCode.error = state.message
                    }
                }
            }
        }
    }

    private fun navigateToRegisterActivity() {
        startActivity(Intent(this, HomeMemberRegisterActivity::class.java).apply {
            putExtra(HomeMemberRegisterActivity.SELECTED_PACKAGE, selectedPackage)
        })
    }

    private fun showLoading(b: Boolean){
        binding.tvTitle.visibility = if(b) View.GONE else View.VISIBLE
        binding.tvDescription.visibility = if(b) View.GONE else View.VISIBLE
        binding.layoutReferralCode.visibility = if(b) View.GONE else View.VISIBLE
        binding.btnContinue.visibility = if(b) View.GONE else View.VISIBLE
        binding.loadingOverlay.visibility = if(!b) View.GONE else View.VISIBLE

    }

    companion object {
        const val SELECTED_PACKAGE = "selected_package"
    }
}