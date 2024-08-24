package com.dicoding.membership.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.databinding.ActivitySplashBinding
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
        splashViewModel.getLoginStatus().observe(this) { isLogin ->
            if (isLogin) {
                Handler().postDelayed({
                    val intentToHome = Intent(this, MainActivity::class.java)
                    intentToHome.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentToHome)
                }, DELAY.toLong())
            } else {
                Handler().postDelayed({
                    val intentToLogin = Intent(this, WelcomeActivity::class.java)
                    intentToLogin.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentToLogin)
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