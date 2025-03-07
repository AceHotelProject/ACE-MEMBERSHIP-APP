package com.dicoding.membership.view.dashboard.profile.detail.detail

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityProfileDetailBinding
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import com.dicoding.membership.view.dashboard.profile.detail.detail.ubahprofil.UbahProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailActivity : AppCompatActivity(){
    private lateinit var binding: ActivityProfileDetailBinding
    private val viewModel: ProfileDetailViewModel by viewModels()
    private var currentUser: User? = null // Add this to store the current user
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        buttonHandler()
        observeUserData()

        userId = intent.getStringExtra(EXTRA_USER_ID) ?: return
        viewModel.getUserData(userId)
    }

    private fun buttonHandler() {
        binding.btnClose.setOnClickListener {
            finish()
        }
        binding.btnEdit.setOnClickListener {
            currentUser?.let { user ->
                val intent = Intent(this, UbahProfileActivity::class.java).apply {
                    putExtra(UbahProfileActivity.USER_DATA, user)
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
                    Log.d("Activity debug", "Data gathered: ${resource.data}")
                    resource.data?.let { user ->
                        currentUser = user // Store the current user
                        if(user.isMember && user.isValidated){
                            updateUserUI(user)
                        } else if(user.isMember && !user.isValidated){
                            pendingMemberLayout()
                        } else {
                            nonMemberLayout()
                        }

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
                }
            }
        }
    }

    private fun updateUserUI(user: User) {
        with(binding) {
            layoutNonMember.visibility = View.GONE
            when (user.role) {
                "admin", "merchant", "receptionist" -> {
                    profileDetailLayoutNonuser.visibility = View.VISIBLE
                    profileDetailLayoutUser.visibility = View.GONE
                    btnEdit.visibility = View.GONE

                    detailProfilTipePengguna.text = user.role
                    detailProfilEmail1.text = user.email
                }
                else -> {
                    profileDetailLayoutNonuser.visibility = View.GONE
                    profileDetailLayoutUser.visibility = View.VISIBLE

                    detailProfilNamaLengkap.text = user.name
                    detailProfilNik.text = user.citizenNumber
                    detailProfilNomorTelepon.text = user.phone
                    detailProfilEmail.text = user.email
                    detailProfilAlamat.text = user.address

                    displayUserImage(user.pathKTP)
                }
            }
        }
    }

    private fun displayUserImage(imagePath: String?) {
        if (imagePath.isNullOrEmpty()) {
            Glide.with(this)
                .load(R.drawable.ktp_example)
                .into(binding.detailProfileKtpImageView)
            return
        }

        Glide.with(this)
            .load(imagePath)  // Glide can handle both URIs and file paths
            .placeholder(R.drawable.ktp_example)
            .error(R.drawable.ktp_example)
            .into(binding.detailProfileKtpImageView)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.detailLayoutSv.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun nonMemberLayout(){
        binding.detailLayoutSv.visibility = View.GONE
        binding.loadingOverlay.visibility = View.GONE
        binding.layoutNonMember.visibility = View.VISIBLE
        binding.btnDaftar.setOnClickListener {
            viewModel.userData.value?.let { user ->
                startActivity(Intent(this, HomeMemberLevelActivity::class.java).apply {
                    putExtra(HomeMemberLevelActivity.EXTRA_USER_ID, user.data?.id)
                })
            }
        }

    }
    private fun pendingMemberLayout(){
        binding.detailLayoutSv.visibility = View.GONE
        binding.loadingOverlay.visibility = View.GONE
        binding.layoutNonMember.visibility = View.GONE
        binding.layoutPending.visibility = View.VISIBLE
        binding.btnRefresh.setOnClickListener {
            binding.layoutPending.visibility = View.GONE
            viewModel.getUserData(userId)
        }

    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}