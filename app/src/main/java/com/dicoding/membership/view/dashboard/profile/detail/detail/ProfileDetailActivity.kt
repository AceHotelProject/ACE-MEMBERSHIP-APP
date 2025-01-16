package com.dicoding.membership.view.dashboard.profile.detail.detail

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.isInternetAvailable
import com.dicoding.membership.databinding.ActivityProfileDetailBinding
import com.dicoding.membership.view.dashboard.member.detailmember.ubahprofile.UbahProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailActivity : AppCompatActivity(){
    private lateinit var binding: ActivityProfileDetailBinding
    private val viewModel: ProfileDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeUserData()
        setupEditButton()

        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return
        viewModel.getUserData(userId)
    }

    private fun setupUI() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupEditButton() {
        binding.btnEdit.setOnClickListener {
            val userId = intent.getStringExtra(EXTRA_USER_ID)
            userId?.let {
                val intent = Intent(this, UbahProfileActivity::class.java).apply {
                    putExtra(UbahProfileActivity.USER_ID, it)
                }
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = intent.getStringExtra(EXTRA_USER_ID)
        userId?.let {
            viewModel.getUserData(it)
        }
    }

    private fun observeUserData() {
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { user ->
                        updateUserUI(user)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    if (!isInternetAvailable(this)) {
                        showToast(getString(R.string.check_internet))
                    } else {
                        showToast(resource.message.toString())
                    }
                }
                is Resource.Message -> {
                    showLoading(false)
                    //Timber.tag("ProfileDetailActivity").d(resource.message)
                }
            }
        }
    }

    private fun updateUserUI(user: User) {
        with(binding) {
            when (user.role) {
                "admin", "merchant", "receptionist" -> {
                    profileDetailLayoutNonuser.visibility = View.VISIBLE
                    profileDetailLayoutUser.visibility = View.GONE

                    detailProfilTipePengguna.text = user.role
                    detailProfilEmail1.text = user.email
                }
                else -> {
                    profileDetailLayoutNonuser.visibility = View.GONE
                    profileDetailLayoutUser.visibility = View.VISIBLE

                    detailProfilNamaLengkap.text = user.name
                    detailProfilNik.text = user.citizen_number
                    detailProfilNomorTelepon.text = user.phone
                    detailProfilEmail.text = user.email

                    Glide.with(this@ProfileDetailActivity)
                        .load(user.id_picture_path)
                        .placeholder(R.drawable.ktp_example)
                        .error(R.drawable.ktp_example)
                        .into(detailProfileKtpImageView)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnClose.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.detailLayoutSv.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.detailProfilTitle.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}