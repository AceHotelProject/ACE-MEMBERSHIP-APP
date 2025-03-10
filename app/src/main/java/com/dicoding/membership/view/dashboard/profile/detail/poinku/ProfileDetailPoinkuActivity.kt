package com.dicoding.membership.view.dashboard.profile.detail.poinku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.Points
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityProfileDetailPoinkuBinding
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailActivity
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailActivity.Companion
import com.dicoding.membership.view.dashboard.profile.detail.poinku.terima.TerimaPoinActivity
import com.dicoding.membership.view.dashboard.profile.detail.poinku.transfer.TransferPoinActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileDetailPoinkuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailPoinkuBinding
    private val viewModel: ProfileDetailPoinkuViewModel by viewModels()
    private lateinit var historyAdapter: PointHistoryAdapter
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailPoinkuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Disable the SwipeRefreshLayout's default indicator
        binding.swipeRefresh.isRefreshing = false
        userId = intent.getStringExtra(EXTRA_USER_ID) ?: return
        viewModel.getUserData(userId)
        observeUserData()



        //functions
        setupRecyclerView()
        setupClickListeners()
        setupSwipeRefresh()

        //observer moved to observe user data


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
                        if(!user.isMember){
                            nonMemberLayout()
                        } else if(!user.isValidated){
                            pendingMemberLayout()
                        } else {
                            viewModel.getUserPoints(userId) // Add this line
                            viewModel.getUserHistory(userId)
                            setupObservers()
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

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // When user swipes to refresh, reset the indicator immediately
            binding.swipeRefresh.isRefreshing = false

            // Show our custom loading overlay instead
            showLoading(true)

            // Get the data
            val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return@setOnRefreshListener
            viewModel.refreshData(userId)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            var pointsLoaded = false
            var historyLoaded = false

            // Observe loading state
            launch {
                viewModel.isLoading.collect { isLoading ->
                    binding.swipeRefresh.isRefreshing = false
                }
            }

            launch {
                viewModel.points.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading(true)
                        is Resource.Success -> {
                            pointsLoaded = true
                            if (historyLoaded) showLoading(false)
                            resource.data?.let { updatePointsUI(it) }
                        }
                        is Resource.Error -> {
                            pointsLoaded = true
                            if (historyLoaded) showLoading(false)
                        }
                        else -> {}
                    }
                }
            }

            launch {
                viewModel.userHistory.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading(true)
                        is Resource.Success -> {
                            historyLoaded = true
                            resource.data?.let { history ->
                                if (history.history.isEmpty()) {
                                    // Show empty state
                                    binding.riwayatKosong.visibility = View.VISIBLE
                                    binding.historyRecyclerview.visibility = View.GONE
                                } else {
                                    binding.riwayatKosong.visibility = View.GONE
                                    binding.historyRecyclerview.visibility = View.VISIBLE
                                    historyAdapter.updateItems(history.history)
                                }
                            }
                            if (pointsLoaded) showLoading(false)

                        }
                        is Resource.Error -> {
                            historyLoaded = true
                            if (pointsLoaded) showLoading(false)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return
        historyAdapter = PointHistoryAdapter(userId)
        binding.historyRecyclerview.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@ProfileDetailPoinkuActivity)
        }
    }

    private fun setupClickListeners() {
        val userId = intent.getStringExtra(EXTRA_USER_ID)

        binding.layoutTerimaButton.setOnClickListener {
            startActivity(Intent(this, TerimaPoinActivity::class.java).apply {
                putExtra(TerimaPoinActivity.EXTRA_USER_ID, userId)
            })
        }

        binding.layoutTransferButton.setOnClickListener {
            val currentPoints = viewModel.points.value.data
            if (currentPoints != null) {
                startActivity(Intent(this, TransferPoinActivity::class.java).apply {
                    putExtra(TransferPoinActivity.EXTRA_USER_ID, userId)
                    putExtra(TransferPoinActivity.EXTRA_POINTS, currentPoints.points.toString())
                })
            } else {
                showToast("Point data is not yet available")
            }
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun updatePointsUI(points: Points) {
        with(binding) {
            poinkuJumlahPoinku.text = points.points.toString()
            poinkuPoinTerimaJumlah.text = points.totalPointIn.toString()
            poinkuPoinTransferJumlah.text = points.totalPointOut.toString()
            poinkuKeuntunganReferralJumlah.text = points.totalPointReferral.toString()
        }
    }

    private fun nonMemberLayout() {
        binding.loadingOverlay.visibility = View.GONE
        binding.layoutPoinku.visibility = View.GONE
        binding.layoutNonMember.visibility = View.VISIBLE
        binding.btnDaftar.setOnClickListener {
            viewModel.userData.value?.let { user ->
                startActivity(Intent(this, HomeMemberLevelActivity::class.java).apply {
                    putExtra(HomeMemberLevelActivity.EXTRA_USER_ID, user.data?.id)
                })
            }
        }
    }

    private fun pendingMemberLayout() {
        binding.loadingOverlay.visibility = View.GONE
        binding.layoutPoinku.visibility = View.GONE
        binding.layoutPending.visibility = View.VISIBLE
        binding.btnRefreshPending.setOnClickListener {
            binding.layoutPending.visibility = View.GONE
            viewModel.getUserData(userId)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.layoutPoinku.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}