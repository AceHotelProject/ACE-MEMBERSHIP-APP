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
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityProfileDetailPoinkuBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailPoinkuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //functions
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: return
        viewModel.getUserPoints(userId)
        viewModel.getUserHistory(userId)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            var pointsLoaded = false
            var historyLoaded = false

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
                            if (pointsLoaded) showLoading(false)
                            resource.data?.let { history ->
                                historyAdapter.updateItems(history.history)
                            }
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
            viewModel.points.value.data?.let { points ->
                startActivity(Intent(this, TransferPoinActivity::class.java).apply {
                    putExtra(TransferPoinActivity.EXTRA_USER_ID, userId)
                    putExtra(TransferPoinActivity.EXTRA_POINTS, points.points.toString())
                })
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

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.layoutPoinku.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}