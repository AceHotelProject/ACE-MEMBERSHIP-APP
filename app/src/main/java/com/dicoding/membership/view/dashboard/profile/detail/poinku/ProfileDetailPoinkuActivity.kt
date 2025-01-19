package com.dicoding.membership.view.dashboard.profile.detail.poinku

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityProfileDetailPoinkuBinding
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailActivity
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailActivity.Companion
import com.dicoding.membership.view.dashboard.profile.detail.poinku.transfer.TransferPoinActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailPoinkuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailPoinkuBinding
    private val viewModel: ProfileDetailPoinkuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailPoinkuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //functions
        setupClickListeners()
        viewModel.getUserData()
    }

    private fun setupClickListeners() {
        binding.layoutTransferButton.setOnClickListener {
            viewModel.userData.value?.let { loginDomain ->
                val intent = Intent(this, TransferPoinActivity::class.java).apply {
                    putExtra(TransferPoinActivity.EXTRA_USER_ID, loginDomain.user.id)
                }
                startActivity(intent)
            }
        }
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnClose.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.layoutPoinku.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.upperBar.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}