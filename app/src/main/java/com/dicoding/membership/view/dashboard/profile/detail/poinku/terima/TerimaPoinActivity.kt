package com.dicoding.membership.view.dashboard.profile.detail.poinku.terima

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityTerimaPoinBinding
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TerimaPoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTerimaPoinBinding
    private val viewModel: TerimaPoinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTerimaPoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // User id passed from previous activity
        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return

        setupUI()
        setupObservers()

        viewModel.getUserData(userId)
    }

    private fun setupUI() {
        binding.btnClose.setOnClickListener {
            finish()
        }
        binding.terimaButtonCek.setOnClickListener {
            // Implement status check functionality
        }
        binding.btnCopy.setOnClickListener {
            // Get the phone number from the TextView
            val phoneNumber = binding.terimaNomorHp.text.toString()

            // Get the clipboard manager
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // Create a ClipData object to store the text
            val clipData = ClipData.newPlainText("phone number", phoneNumber)
            // Set the clipdata to clipboard
            clipboardManager.setPrimaryClip(clipData)

            // Show feedback to user
            Toast.makeText(this, "Phone number copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        binding.terimaButtonCek.setOnClickListener {
            // Implementasi function cek here

            // Status Activity
            val statusTemplate = StatusTemplate(
                title = "Poin Diterima",
                description = "Poin telah berhasil diterima",
                showCoupon = false,
                promoCode = "",
                expiryTime = "",
                buttonText = "Selesai"
            )

            val intent = Intent(this, StatusTemplateActivity::class.java).apply {
                putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userData.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            showLoading(false)
                            binding.terimaNomorHp.text = resource.data?.phone
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            Toast.makeText(
                                this@TerimaPoinActivity,
                                "Error: ${resource.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {
                            showLoading(true)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun showLoading(boolean: Boolean){
        binding.progressBar.visibility = if(boolean) View.VISIBLE else View.GONE
        binding.linearLayout.visibility = if(boolean) View.GONE else View.VISIBLE
        binding.btnClose.visibility = if(boolean) View.GONE else View.VISIBLE
        binding.terimaTitle.visibility = if(boolean) View.GONE else View.VISIBLE
        binding.terimaButtonCek.visibility = if(boolean) View.GONE else View.VISIBLE

    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}