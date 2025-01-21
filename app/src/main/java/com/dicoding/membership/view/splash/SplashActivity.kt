package com.dicoding.membership.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.databinding.ActivitySplashBinding
import com.dicoding.membership.view.login.LoginActivity
import com.dicoding.membership.view.verification.VerificationActivity
import com.dicoding.membership.view.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        checkUserInfo()
    }

    private fun checkUserInfo() {
        splashViewModel.getAccessToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                // Jika ada token, cek status email verification
                splashViewModel.getUser().observe(this) { user ->
                    Log.d("CheckUserInfo", "User data: $user")
                    Handler().postDelayed({
                        when {
                            user.user.isEmailVerified -> {
                                // Email belum terverifikasi, arahkan ke verification
                                startActivity(Intent(this, LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    putExtra(VerificationActivity.EXTRA_USER_ID, user.user.id)
                                    putExtra(VerificationActivity.EXTRA_AUTO_SEND_OTP, true)
                                })
                            }
                            else -> {
                                // Email sudah terverifikasi, lanjut ke main
                                startActivity(Intent(this, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                            }
                        }
                        finish()
                    }, DELAY.toLong())
                }
            } else {
                // Tidak ada token, arahkan ke welcome
                Handler().postDelayed({
                    startActivity(Intent(this, WelcomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }, DELAY.toLong())
            }
        }
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }

    companion object {
        private const val DELAY = 4000
    }
}