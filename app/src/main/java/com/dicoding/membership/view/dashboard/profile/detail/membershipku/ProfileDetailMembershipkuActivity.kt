package com.dicoding.membership.view.dashboard.profile.detail.membershipku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.core.data.source.Resource
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityProfileDetailMembershipkuBinding
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailMembershipkuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailMembershipkuBinding
    private val viewModel: ProfileDetailMembershipkuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailMembershipkuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return
        viewModel.getUserData(userId)

        //
        observeUserData()
        buttonHandler()
    }

    private fun buttonHandler() {
        binding.btnClose.setOnClickListener {
            finish()
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
                        if(user.isMember){
                            //Implementation
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

    private fun showLoading(b: Boolean) {
        binding.loadingOverlay.visibility = if (b) View.VISIBLE else View.GONE
        binding.svMain.visibility = if (!b) View.VISIBLE else View.GONE
        binding.membershipkuButtonPerpanjang.visibility = if (!b) View.VISIBLE else View.GONE
    }

    private fun nonMemberLayout(){
        binding.membershipkuButtonPerpanjang.visibility = View.GONE
        binding.svMain.visibility = View.GONE
        binding.layoutNonMember.visibility = View.VISIBLE
        binding.btnDaftar.setOnClickListener {
            viewModel.userData.value?.let { user ->
                startActivity(Intent(this, HomeMemberLevelActivity::class.java).apply {
                    putExtra(HomeMemberLevelActivity.EXTRA_USER_ID, user.data?.id)
                })
            }
        }

    }
    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}