package com.dicoding.membership.view.dashboard.home.member.mreferral

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.databinding.ActivityHomeMemberReferralBinding

class HomeMemberReferralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeMemberReferralBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeMemberReferralBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}