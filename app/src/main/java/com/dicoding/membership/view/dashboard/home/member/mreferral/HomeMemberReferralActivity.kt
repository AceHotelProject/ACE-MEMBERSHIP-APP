package com.dicoding.membership.view.dashboard.home.member.mreferral

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.databinding.ActivityHomeMemberReferralBinding
import com.dicoding.membership.view.dashboard.home.member.mregister.HomeMemberRegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeMemberReferralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeMemberReferralBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberReferralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        //implementasi kode referral
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Continue Button implementation
        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, HomeMemberRegisterActivity::class.java).apply {
                // pass data to next activity here.
            })
        }


    }
}