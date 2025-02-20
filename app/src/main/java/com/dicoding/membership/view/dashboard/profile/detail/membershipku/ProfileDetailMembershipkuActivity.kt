package com.dicoding.membership.view.dashboard.profile.detail.membershipku

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityProfileDetailMembershipkuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailMembershipkuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailMembershipkuBinding
    private val viewModel: ProfileDetailMembershipkuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailMembershipkuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}