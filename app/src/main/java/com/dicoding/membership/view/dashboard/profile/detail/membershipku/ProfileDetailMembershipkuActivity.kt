package com.dicoding.membership.view.dashboard.profile.detail.membershipku

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.request.transition.Transition
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityProfileDetailMembershipkuBinding
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import com.dicoding.membership.view.dashboard.profile.detail.membershipku.upgrade.UpgradeMembershipActivity
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ProfileDetailMembershipkuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailMembershipkuBinding
    private val viewModel: ProfileDetailMembershipkuViewModel by viewModels()
    private var userId: String? = null
    private var userStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailMembershipkuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra(EXTRA_USER_ID)
        if (userId == null) {
            showToast("User ID not found")
            finish()
            return
        }

        // Set up observers first, so we can respond to any data changes
        setupObservers()

        // Set up button handlers
        setupButtons()

        // Initialize views based on current data
        initializeView()
    }

    private fun initializeView() {
        // First check if we already have membership data
        val membership = viewModel.activeMembership.value

        if (membership != null) {
            // We have membership data, show it immediately
            updateMembershipUI(membership)
            showMemberLayout()
            userStatus = "active"
        } else {
            // No membership data yet, check user data from viewModel
            val userData = viewModel.userData.value

            if (userData is Resource.Success) {
                // We have user data, determine which layout to show
                userData.data?.let { user ->
                    if (user.isMember && user.isValidated) {
                        // User is a member but no membership data, do a refresh
                        userStatus = "active"
                        showLoading(true)
                        hideAllLayouts()
                        userId?.let { viewModel.getUserData(it) }
                    } else if (user.isMember) {
                        // User is a member but not validated (pending)
                        userStatus = "pending"
                        showPendingLayout()
                    } else {
                        // User is not a member
                        userStatus = "none"
                        nonMemberLayout()
                    }
                } ?: run {
                    // User data exists but is null
                    nonMemberLayout()
                }
            } else {
                // No user data yet, load it
                showLoading(true)
                hideAllLayouts()
                userId?.let { viewModel.getUserData(it) }
            }
        }
    }

    private fun hideAllLayouts() {
        binding.svMain.visibility = View.GONE
        binding.layoutNonMember.visibility = View.GONE
        binding.layoutPending.visibility = View.GONE
        binding.membershipkuButtonPerpanjang.visibility = View.GONE
    }

    private fun setupButtons() {
        // Close button
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Upgrade button
        binding.btnUpgrade.setOnClickListener {
            viewModel.userData.value?.data?.let { user ->
                // Get the current membership data to pass to upgrade activity
                viewModel.activeMembership.value?.let { membership ->
                    val intent = Intent(this, UpgradeMembershipActivity::class.java).apply {
                        putExtra(UpgradeMembershipActivity.EXTRA_USER_ID, user.id)

                        // Pass current membership data
                        membership.image?.getOrNull(0)?.let {
                            putExtra(UpgradeMembershipActivity.EXTRA_MEMBERSHIP_IMAGE, it)
                        }
                        putExtra(UpgradeMembershipActivity.EXTRA_MEMBERSHIP_TYPE, membership.type)

                        // Format and pass expiry date
                        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
                        membership.expiryDate?.let { date ->
                            putExtra(UpgradeMembershipActivity.EXTRA_MEMBERSHIP_EXPIRY_FORMATTED,
                                "Exp ${dateFormat.format(date)}")
                        }

                        // Pass price for comparison in the upgrade activity
                        putExtra(UpgradeMembershipActivity.EXTRA_MEMBERSHIP_PRICE, membership.price!!.toInt())
                        Log.d("debug membershipku", "PRICE ${membership.price}")
                    }
                    startActivity(intent)
                } ?: run {
                    // If no membership data available, just pass user ID
                    startActivity(Intent(this, UpgradeMembershipActivity::class.java).apply {
                        putExtra(UpgradeMembershipActivity.EXTRA_USER_ID, user.id)
                    })
                }
            }
        }

        // Refresh button - gets new user data and triggers membership fetch
        binding.btnRefresh.setOnClickListener {
            userId?.let { id ->
                if (isInternetAvailable(this)) {
                    // Only refresh when the button is explicitly clicked
                    hideAllLayouts()
                    showLoading(true)
                    viewModel.getUserData(id)
                    showToast("Refreshing membership data...")
                } else {
                    showToast(getString(R.string.check_internet))
                }
            }
        }

        // Refresh button in pending layout
        binding.btnRefreshPending.setOnClickListener {
            userId?.let { id ->
                if (isInternetAvailable(this)) {
                    hideAllLayouts()
                    showLoading(true)
                    viewModel.getUserData(id)
                    showToast("Refreshing membership data...")
                } else {
                    showToast(getString(R.string.check_internet))
                }
            }
        }
        binding.membershipkuButtonPerpanjang.setOnClickListener {
            viewModel.activeMembership.value?.let { membership ->
                // Call subscribe function with the current membership type
                viewModel.subscribe(membership.id)
            } ?: run {
                showToast("Membership data not available")
            }
        }
    }

    private fun setupObservers() {
        // Observe user data
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Keep current layout if already showing, otherwise show loading
                    if (binding.svMain.visibility != View.VISIBLE &&
                        binding.layoutNonMember.visibility != View.VISIBLE &&
                        binding.layoutPending.visibility != View.VISIBLE) {
                        showLoading(true)
                    }
                }
                is Resource.Success -> {
                    resource.data?.let { user ->
                        if (user.isMember && user.isValidated) {
                            // User is an active member
                            userStatus = "active"
                            // No need to do anything here, we'll let the activeMembership observer handle UI updates
                        } else if (user.isMember) {
                            // User is a pending member
                            userStatus = "pending"
                            showPendingLayout()
                            showLoading(false)
                        } else {
                            // User is not a member
                            userStatus = "none"
                            nonMemberLayout()
                            showLoading(false)
                        }
                    } ?: run {
                        showLoading(false)
                        showToast("Failed to load user data")
                        nonMemberLayout()
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    if (!isInternetAvailable(this)) {
                        showToast(getString(R.string.check_internet))
                    } else {
                        showToast(resource.message.toString())
                    }

                    // If we already have membership data, don't change the layout
                    if (viewModel.activeMembership.value == null) {
                        nonMemberLayout()
                    }
                }
                is Resource.Message -> {
                    showLoading(false)
                    showToast(resource.message.toString())

                    // If we already have membership data, don't change the layout
                    if (viewModel.activeMembership.value == null) {
                        nonMemberLayout()
                    }
                }
            }
        }

        // Observe membership data from remote API - this is just for logging
        viewModel.membershipData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // No UI changes needed
                }
                is Resource.Success -> {
                    Log.d("Activity", "Fetched membership with images: ${resource.data?.image}")
                }
                is Resource.Error -> {
                    if (userStatus == "active") {
                        Log.e("Activity", "Error fetching membership: ${resource.message}")
                        // Only show toast if this was from a manual refresh
                        if (binding.loadingOverlay.visibility == View.VISIBLE) {
                            showToast("Error fetching membership: ${resource.message}")
                        }
                    }
                }
                is Resource.Message -> {
                    // No UI changes needed
                }
            }
        }

        // Observe store membership status
        viewModel.storeMembershipStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // No UI changes needed
                }
                is Resource.Success -> {
                    // No UI changes needed, activeMembership observer will handle it
                }
                is Resource.Error -> {
                    // Only show error if this was from a manual refresh
                    if (binding.loadingOverlay.visibility == View.VISIBLE) {
                        showLoading(false)
                        showToast("Error: ${resource.message}")
                    }
                }
                is Resource.Message -> {
                    // Only show message if this was from a manual refresh
                    if (binding.loadingOverlay.visibility == View.VISIBLE) {
                        showLoading(false)
                        showToast(resource.message.toString())
                    }
                }
            }
        }

        // Observe local database changes - this is what drives our UI for active members
        viewModel.activeMembership.observe(this) { membership ->
            if (membership != null) {
                // We have membership data to show
                if (userStatus == "active" || userStatus == null) {
                    userStatus = "active"
                    updateMembershipUI(membership)
                    showMemberLayout()
                    showLoading(false)
                }
            } else if (userStatus == "active" &&
                viewModel.membershipData.value !is Resource.Loading &&
                viewModel.storeMembershipStatus.value !is Resource.Loading) {
                // We're expecting membership data for an active user but don't have it after loading
                // Only change UI if we're showing the loading overlay (manual refresh)
                if (binding.loadingOverlay.visibility == View.VISIBLE) {
                    showLoading(false)
                    showToast("No membership data found")
                    nonMemberLayout()
                }
            }
        }

        viewModel.subscribeResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)

                    // Create the success status template
                    val statusTemplate = StatusTemplate(
                        title = "Perpanjang Berhasil",
                        description = "Perpanjang anda telah berhasil. Silahkan hubungi admin untuk pembayaran",
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
                    showToast(resource.message ?: "Failed to renew membership")
                }
                is Resource.Message -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun updateMembershipUI(membership: MembershipLocal) {
        // Update membership tier name
        binding.membershipkuTier.text = membership.type ?: "Unknown Membership"

        // Update expiry date with format "Exp 28 Desember 2023"
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id")) // Indonesian locale
        val expiryDateText = membership.expiryDate?.let { "Exp ${dateFormat.format(it)}" } ?: "Unknown"
        binding.membershipkuExpiry.text = expiryDateText

        // Load the first image as background for the membership card layout
        membership.image?.getOrNull(0)?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        // Set the background
                        binding.membershipCardLayout.background = resource

                        // Get image dimensions
                        val width = resource.intrinsicWidth
                        val height = resource.intrinsicHeight

                        // Calculate the aspect ratio of the image
                        val aspectRatio = height.toFloat() / width.toFloat()

                        // Set the height based on the width of the layout and the aspect ratio
                        binding.membershipCardLayout.post {
                            val layoutWidth = binding.membershipCardLayout.width
                            val newHeight = (layoutWidth * aspectRatio).toInt()

                            // Update the layout parameters
                            val params = binding.membershipCardLayout.layoutParams
                            params.height = newHeight
                            binding.membershipCardLayout.layoutParams = params
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        binding.membershipCardLayout.background = ContextCompat.getDrawable(
                            this@ProfileDetailMembershipkuActivity,
                            R.drawable.orange_card_half
                        )

                        // Reset height to wrap_content if image is cleared
                        val params = binding.membershipCardLayout.layoutParams
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT
                        binding.membershipCardLayout.layoutParams = params
                    }
                })
        }

        viewModel.userData.value?.data?.let { user ->
            // Calculate used coupons from user data
            val usedCoupons = user.couponUsed?.size ?: 0

            // Calculate remaining coupons
            val remainingCoupons = (membership.maxCoupon ?: 0) - usedCoupons

            // Update UI
            binding.membershipkuSisaKuponAndaJumlah.text = remainingCoupons.toString()
            binding.membershipkuPromoDigunakanJumlah.text = usedCoupons.toString()

            // Update local storage with the calculated remaining coupons
            viewModel.updateRemainingCoupons(membership.id, remainingCoupons)

            // Update point display
            binding.membershipkuTotalPoinJumlah.text = user.point.toString()
        } ?: run {
            // Fallback to using local membership data if user data is not available
            binding.membershipkuSisaKuponAndaJumlah.text = membership.remainingCoupons?.toString() ?: "0"

            val usedCoupons = (membership.maxCoupon ?: 0) - (membership.remainingCoupons ?: 0)
            binding.membershipkuPromoDigunakanJumlah.text = usedCoupons.toString()
        }

        // Update point display if available from user data
        viewModel.userData.value?.data?.let { user ->
            binding.membershipkuTotalPoinJumlah.text = user.point.toString()
        }
        // Show extend button if membership is expired
        Log.d("membership data debug","expiryDate: ${membership.expiryDate}, ${java.util.Date()}")
        binding.membershipkuButtonPerpanjang.visibility = View.VISIBLE
        if (membership.expiryDate?.before(java.util.Date()) == true) {
            binding.membershipkuButtonPerpanjang.isEnabled = true
        } else {
            binding.membershipkuButtonPerpanjang.isEnabled = false
        }
    }

    private fun showLoading(b: Boolean) {
        binding.loadingOverlay.visibility = if (b) View.VISIBLE else View.GONE
        if(b) {
            hideAllLayouts()
        }
    }

    private fun showMemberLayout() {
        binding.svMain.visibility = View.VISIBLE
        binding.layoutNonMember.visibility = View.GONE
        binding.layoutPending.visibility = View.GONE


    }

    private fun showPendingLayout() {
        binding.svMain.visibility = View.GONE
        binding.layoutNonMember.visibility = View.GONE
        binding.layoutPending.visibility = View.VISIBLE
        binding.membershipkuButtonPerpanjang.visibility = View.GONE
    }

    private fun nonMemberLayout() {
        binding.svMain.visibility = View.GONE
        binding.layoutNonMember.visibility = View.VISIBLE
        binding.layoutPending.visibility = View.GONE
        binding.membershipkuButtonPerpanjang.visibility = View.GONE

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