package com.dicoding.membership.view.dashboard.profile.detail.referralku

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.databinding.ActivityProfileDetailReferralkuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailReferralkuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailReferralkuBinding
    private val viewModel: ProfileDetailReferralkuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailReferralkuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}