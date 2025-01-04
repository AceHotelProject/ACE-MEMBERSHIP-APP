package com.dicoding.membership.view.dashboard.member.listeditmember

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityListEditMemberBinding
import com.dicoding.membership.databinding.ActivityMainBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.dashboard.MainViewModel
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory

class ListEditMemberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListEditMemberBinding

    private lateinit var splitInstallManager: SplitInstallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splitInstallManager = SplitInstallManagerFactory.create(this)

        binding = ActivityListEditMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleMenuButton()
    }

    private fun handleMenuButton() {
        binding.btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}