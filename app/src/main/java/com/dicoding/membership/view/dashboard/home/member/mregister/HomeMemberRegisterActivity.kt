package com.dicoding.membership.view.dashboard.home.member.mregister

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.databinding.ActivityHomeMemberRegisterBinding

class HomeMemberRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeMemberRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeMemberRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}