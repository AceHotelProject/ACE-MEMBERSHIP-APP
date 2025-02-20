package com.dicoding.membership.view.dashboard.profile.detail.referralku

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.membership.databinding.ActivityProfileDetailReferralkuBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileDetailReferralkuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailReferralkuBinding
    private val viewModel: ProfileDetailReferralkuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailReferralkuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeReferralToken()
        setupSwipeRefresh()
        observeLoading()  // Add this line
        viewModel.getReferralToken()
    }

    private fun setupUI() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.referralkuButtonCopy.setOnClickListener {
            val token = binding.referralkuKodeReferral.text.toString()
            copyToClipboard(token)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getReferralToken()
        }
    }

    private fun observeLoading() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.swipeRefresh.isRefreshing = isLoading
                //binding.linearLayout4.visibility = if(isLoading) View.GONE else View.VISIBLE
            }
        }
    }

    private fun observeReferralToken() {
        lifecycleScope.launch {
            viewModel.referralToken.collect { result ->
                result?.onSuccess { referralToken ->
                    binding.referralkuKodeReferral.text = referralToken.token
                }?.onFailure { exception ->
                    // Handle error - show toast or snackbar
                    Toast.makeText(
                        this@ProfileDetailReferralkuActivity,
                        "Failed to load referral token: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Referral Token", text)
        clipboard.setPrimaryClip(clip)

        // Show feedback to user
//        Toast.makeText(
//            this,
//            "Referral code copied to clipboard",
//            Toast.LENGTH_SHORT
//        ).show()
    }
    
    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}