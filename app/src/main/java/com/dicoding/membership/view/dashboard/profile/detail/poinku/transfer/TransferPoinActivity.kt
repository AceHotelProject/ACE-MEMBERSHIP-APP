package com.dicoding.membership.view.dashboard.profile.detail.poinku.transfer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.databinding.ActivityTransferPoinBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferPoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransferPoinBinding
    private val viewModel: TransferPoinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferPoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //user id passed from previous activity
        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return

        //functions
        setupClickListeners()
        setupUserDataObserver()
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.buttonCek.setOnClickListener {
            val kodePengguna = binding.transferMasukkanKodePenggunaVal.text.toString()

            if (kodePengguna.isBlank()) {
                binding.transferMasukkanKodePengguna.error = "Kode pengguna tidak boleh kosong"
                return@setOnClickListener
            }

            // Call viewModel to get user data
            viewModel.getUserDataByPhone(kodePengguna)
        }
    }

    private fun setupUserDataObserver() {
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoadingDataPengguna(true)  // Show loading only for data pengguna section
                }
                is Resource.Success -> {
                    showLoadingDataPengguna(false)
                    resource.data?.let { user ->
                        updateUserDataUI(user)
                    }
                }
                is Resource.Error -> {
                    showLoadingDataPengguna(false)
                    // Show error message
                    Toast.makeText(
                        this,
                        resource.message ?: "Terjadi kesalahan",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Clear the UI data on error
                    clearUserDataUI()
                }
                is Resource.Message -> TODO()
            }
        }
    }

    private fun clearUserDataUI() {
        with(binding) {
            transferPenggunaEmail.text = ""
            transferPenggunaNomorHp.text = ""
        }
    }

    private fun updateUserDataUI(user: User) {
        with(binding) {
            transferPenggunaEmail.text = user.email
            transferPenggunaNomorHp.text = user.phone
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnClose.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.layoutTransferSv.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.transferTitle.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showLoadingDataPengguna(isLoading: Boolean) {
        binding.loadingOverlayDataPengguna.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.layoutDataPengguna.visibility = if(isLoading) View.GONE else View.VISIBLE
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}