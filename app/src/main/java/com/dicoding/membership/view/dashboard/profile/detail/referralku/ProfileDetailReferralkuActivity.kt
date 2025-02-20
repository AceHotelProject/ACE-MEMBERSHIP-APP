package com.dicoding.membership.view.dashboard.profile.detail.referralku

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityProfileDetailReferralkuBinding
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailActivity
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailActivity.Companion
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

        val userId = intent.getStringExtra(ProfileDetailActivity.EXTRA_USER_ID) ?: return
        viewModel.getUserData(userId)

        observeUserData()

        buttonHandler()
        observeReferralToken()
        setupSwipeRefresh()
        observeLoading()  // Add this line
        viewModel.getReferralToken()
    }

    private fun buttonHandler() {
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
                showLoading(true)
            }
        }
    }

    private fun observeUserData() {
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    Log.d("Activity debug", "Data gathered: ${resource.data}")
                    resource.data?.let { user ->
                        if(!user.isMember){
                            nonMemberLayout()
                        }

                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    if (!isInternetAvailable(this)) {
                        showToast(getString(R.string.check_internet))
                    } else {
                        showToast(resource.message.toString())
                    }
                }
                is Resource.Message -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(b: Boolean){
        binding.loadingOverlay.visibility = if(b) View.VISIBLE else View.GONE
        binding.linearLayout4.visibility = if(!b) View.VISIBLE else View.GONE
    }

    private fun nonMemberLayout(){
        binding.linearLayout4.visibility = View.GONE
        binding.loadingOverlay.visibility = View.GONE
        binding.layoutNonMember.visibility = View.VISIBLE
        binding.btnDaftar.setOnClickListener {
            viewModel.userData.value?.let { user ->
                startActivity(Intent(this, HomeMemberLevelActivity::class.java).apply {
                    putExtra(HomeMemberLevelActivity.EXTRA_USER_ID, user.data?.id)
                })
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