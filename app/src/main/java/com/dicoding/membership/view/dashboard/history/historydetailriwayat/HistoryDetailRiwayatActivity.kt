package com.dicoding.membership.view.dashboard.history.historydetailriwayat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityHistoryDetailRiwayatBinding
import com.dicoding.membership.view.dashboard.profile.detail.detail.ubahprofil.UbahProfileActivity
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailRiwayatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailRiwayatBinding
    private val viewModel: HistoryDetailMemberViewModel by viewModels()
    private var currentUser: User? = null
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCloseButton()
        setupMenuButton()
        setupSwipeRefresh()

        // Get user ID and load data
        currentUserId = intent.getStringExtra(EXTRA_USER_ID)
        currentUserId?.let { userId ->
            viewModel.getUserData(userId)
        }

        observeUserData()
        observeVerificatorData()
        observeDeleteResult()
        setupEditButton()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setColorSchemeResources(
                R.color.orange_100,
                R.color.green,
                R.color.dark_grey
            )

            setOnRefreshListener {
                // Reload user data when swipe refreshing
                currentUserId?.let { userId ->
                    viewModel.getUserData(userId)
                }
            }
        }
    }

    private fun setupCloseButton() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupEditButton() {
        binding.fbUbahProfile.setOnClickListener {
            currentUser?.let { user ->
                val intent = Intent(this, UbahProfileActivity::class.java).apply {
                    putExtra(UbahProfileActivity.USER_DATA, user)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupMenuButton() {
        binding.btnMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.menu_member_detail, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        showDeleteDialog()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun showDeleteDialog() {
        val dialog = GlobalTwoButtonDialog().apply {
            setDialogTitle("Hapus User?")
            setDialogMessage("Apakah anda yakin ingin menghapus user ini?")
            setOnYesClickListener {
                currentUserId?.let { userId ->
                    viewModel.deleteUser(userId)
                }
            }
        }
        dialog.show(supportFragmentManager, "DeleteUserDialog")
    }

    private fun observeUserData() {
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.loadingOverlay.visibility = View.VISIBLE
                        binding.scrollableContent.visibility = View.GONE
                    }
                }
                is Resource.Success -> {
                    binding.loadingOverlay.visibility = View.GONE
                    binding.scrollableContent.visibility = View.VISIBLE
                    binding.swipeRefresh.isRefreshing = false

                    resource.data?.let { user ->
                        currentUser = user
                        binding.apply {
                            // Membership type
                            labelMembershipType.text = user.membership?.subscriptionType?.type ?: "Non-Member"

                            // User data
                            tvUserNama.text = user.name
                            tvUserNIK.text = user.citizenNumber ?: "-"
                            tvUserPhone.text = user.phone ?: "-"
                            tvUserAddress.text = user.address ?: "-"
                            tvUserEmail.text = user.email
                            labelStatus.text = user.membership?.status ?: "-"
                            if(user.membership?.status?.equals("active", ignoreCase = true) == true) {
                                // Active status - use green style
                                labelStatus.setBackgroundResource(R.drawable.chip_category_green)
                                labelStatus.setTextColor(ContextCompat.getColor(this@HistoryDetailRiwayatActivity, R.color.green))
                            } else {
                                // Inactive status - use red style
                                labelStatus.setBackgroundResource(R.drawable.chip_category_red)
                                labelStatus.setTextColor(ContextCompat.getColor(this@HistoryDetailRiwayatActivity, R.color.red))
                            }

                            // Format dates using the ViewModel's formatDate function
                            tvExpMember.text = viewModel.formatDate(user.membership?.endDate)
                            tvTglTransaksi.text = viewModel.formatDate(user.membership?.startDate)

                            // Set default value for verificator while waiting for data
                            tvVerifikator.text = "-"

                            // Load KTP image
                            Glide.with(this@HistoryDetailRiwayatActivity)
                                .load(user.pathKTP)
                                .placeholder(R.drawable.ktp_example)
                                .error(R.drawable.ktp_example)
                                .centerCrop()
                                .into(ivKtpImage)

                            Glide.with(this@HistoryDetailRiwayatActivity)
                                .load(user.membership?.paymentProof)
                                .placeholder(R.drawable.image_empty)
                                .error(R.drawable.image_empty)
                                .centerCrop()
                                .into(ivBuktipembayaran)

                            // Log verificator ID for debugging
                            Log.d("HistoryDetail", "VerificatorId: ${user.membership?.verificatorId}")
                        }
                    }
                }
                is Resource.Error -> {
                    binding.loadingOverlay.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun observeVerificatorData() {
        viewModel.verificatorData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // No need to show loading for just the verificator field
                }
                is Resource.Success -> {
                    resource.data?.let { verificator ->
                        // Update the verificator name in the TextView
                        binding.tvVerifikator.text = verificator.email
                        Log.d("HistoryDetail", "Verificator name loaded: ${verificator.name}")
                    }
                }
                is Resource.Error -> {
                    // Keep the default value or show error message if needed
                    binding.tvVerifikator.text = "Tidak ditemukan"
                    Log.e("HistoryDetail", "Error fetching verificator: ${resource.message}")
                }
                else -> {}
            }
        }
    }

    private fun observeDeleteResult() {
        viewModel.deleteUserResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.loadingOverlay.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.loadingOverlay.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this, "User berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    binding.loadingOverlay.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}