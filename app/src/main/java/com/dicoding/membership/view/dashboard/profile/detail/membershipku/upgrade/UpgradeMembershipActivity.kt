package com.dicoding.membership.view.dashboard.profile.detail.membershipku.upgrade

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dicoding.core.data.source.Resource
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityProfileDetailMembershipkuBinding
import com.dicoding.membership.databinding.ActivityUpgradeMembershipBinding
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import com.dicoding.membership.view.dashboard.home.member.mlevel.MembershipLevelAdapter
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpgradeMembershipActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpgradeMembershipBinding
    private val viewModel: UpgradeMembershipViewModel by viewModels()
    private lateinit var adapter: MembershipLevelAdapter
    private var selectedMemberType: String? = null
    private var currentMembershipPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpgradeMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return

        // Get membership data from intent
        val membershipImage = intent.getStringExtra(EXTRA_MEMBERSHIP_IMAGE)
        val membershipType = intent.getStringExtra(EXTRA_MEMBERSHIP_TYPE)
        val membershipExpiry = intent.getStringExtra(EXTRA_MEMBERSHIP_EXPIRY_FORMATTED)
        currentMembershipPrice = intent.getIntExtra(EXTRA_MEMBERSHIP_PRICE, 0)

        // Update UI with membership data
        updateMembershipCard(membershipImage, membershipType, membershipExpiry)

        setupRecyclerView()
        setupViewModel()
        setupClickListeners()
        isButton(false)

        // Load user data
        viewModel.getUserData(userId)
    }

    private fun updateMembershipCard(imageUrl: String?, type: String?, expiry: String?) {
        // Set membership level
        binding.membershipLevel.text = type ?: "Unknown Membership"

        // Set expiry date
        binding.membershipExpiry.text = expiry ?: "Unknown"

        // Load image as background if available
        imageUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        binding.backgroundMembershipImage.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Use default background if image loading fails
                        binding.backgroundMembershipImage.setBackgroundResource(R.drawable.background_mini_ace_silver)
                    }
                })
        }
    }

    private fun setupRecyclerView() {
        adapter = MembershipLevelAdapter()
        binding.rvMemberships.apply {
            adapter = this@UpgradeMembershipActivity.adapter
            layoutManager = LinearLayoutManager(this@UpgradeMembershipActivity)
        }

        adapter.setOnItemSelectedCallback { membership ->
            selectedMemberType = membership.id
            isButton(true)
        }
    }

    private fun setupViewModel() {
        // Observe memberships
        viewModel.subscribeResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)

                    // Create the success status template
                    val statusTemplate = StatusTemplate(
                        title = "Berhasil Upgrade Membership",
                        description = "Berhasil melakukan upgrade membership, silahkan kontak admin untuk pembayaran",
                        showCoupon = false,
                        buttonText = "Selesai"
                    )

                    // Navigate to status screen
                    val intent = Intent(this, StatusTemplateActivity::class.java).apply {
                        putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
                    }
                    startActivity(intent)
                    finish() // Close current activity
                }
                is Resource.Error -> {
                    showLoading(false)
                    showToast(resource.message ?: "Failed to upgrade membership")
                }
                is Resource.Message -> {
                    showLoading(false)
                }
            }
        }
        viewModel.memberships.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.results?.let { memberships ->
                        // Filter memberships that are more expensive or equal to current membership
                        val filteredMemberships = memberships.filter { membership ->
                            val membershipPrice = membership.price ?: 0
                            membershipPrice > currentMembershipPrice
                        }

                        Log.d("debug","Higher than ${currentMembershipPrice}: ${filteredMemberships}")

                        // Sort by price in descending order (most expensive first)
                        val sortedMemberships = filteredMemberships.sortedByDescending { it.price }

                        // Submit the filtered and sorted list
                        adapter.submitList(sortedMemberships)

                        // If no memberships available after filtering, show a message
                        if (sortedMemberships.isEmpty()) {
                            showToast("No upgrade options available")
                            //binding.tvNoUpgrades.visibility = View.VISIBLE
                        } else {
                            //binding.tvNoUpgrades.visibility = View.GONE
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showToast(resource.message ?: "Failed to load memberships")
                    //binding.tvNoUpgrades.visibility = View.VISIBLE
                    //binding.tvNoUpgrades.text = "Failed to load upgrade options"
                }
                is Resource.Message -> {
                    showLoading(false)
                }
            }
        }

        // Load memberships
        viewModel.getMemberships()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            selectedMemberType?.let { type ->
                // Show loading while processing subscription
                showLoading(true)

                // Call subscribe function with selected membership type
                viewModel.subscribe(type)
            }
        }
    }

    private fun isButton(boolean: Boolean) {
        binding.btnContinue.isEnabled = boolean
    }

    private fun showLoading(boolean: Boolean) {
        binding.apply {
            if (boolean) {
                loadingOverlay.visibility = View.VISIBLE
                rvMemberships.visibility = View.GONE
                btnContinue.visibility = View.GONE
            } else {
                loadingOverlay.visibility = View.GONE
                rvMemberships.visibility = View.VISIBLE
                btnContinue.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_MEMBERSHIP_IMAGE = "extra_membership_image"
        const val EXTRA_MEMBERSHIP_TYPE = "extra_membership_type"
        const val EXTRA_MEMBERSHIP_EXPIRY_FORMATTED = "extra_membership_expiry_formatted"
        const val EXTRA_MEMBERSHIP_PRICE = "extra_membership_price"
    }
}