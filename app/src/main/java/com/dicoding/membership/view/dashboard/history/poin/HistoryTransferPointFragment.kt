package com.dicoding.membership.view.dashboard.history.poin

import DashboardPointHistoryAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentHistoryTransferPoinBinding
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import com.dicoding.membership.view.dashboard.profile.detail.membershipku.ProfileDetailMembershipkuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryTransferPointFragment : Fragment() {
    private var _binding: FragmentHistoryTransferPoinBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryTransferPointViewModel by viewModels()
    private lateinit var historyAdapter: DashboardPointHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryTransferPoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUserRole()
    }

    private fun checkUserRole() {
        viewModel.getUserData()

        viewModel.userData.observe(viewLifecycleOwner) { loginDomain ->
            loginDomain?.let { data ->
                val userRole = mapToUserRole(data.user.role)

                Log.d("debug2", "${userRole}")
                // Determine final user role based on role and member status
                val finalUserRole = when (userRole) {
                    UserRole.USER -> {
                        if (loginDomain.user.isMember && loginDomain.user.isValidated) {
                            UserRole.MEMBER
                        } else if(loginDomain.user.isMember){
                            UserRole.PENDINGMEMBER
                        } else {
                            UserRole.NONMEMBER
                        }
                    }
                    UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> userRole
                    else -> userRole
                }

                Log.d("HistoryTransferPointF", "Current User Role: ${finalUserRole.name}")

                // Setup visibility based on user role
                setupUserVisibility(finalUserRole, data.user.id)

                // Proceed with data loading for authorized roles
                when (finalUserRole) {
                    UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER -> {
                        setupRecyclerView(data.user.id)
                        setupSwipeRefresh(data.user.id)
                        viewModel.getUserHistory(data.user.id)

                        // Observe point history data
                        observePointHistory()
                    }
                    else -> {
                        Log.d("HistoryTransferPointF", "Unauthorized role: ${finalUserRole.name}, skipping data load")
                    }
                }
            }
        }
    }

    private fun setupUserVisibility(userRole: UserRole, userId: String) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER -> {
                binding.apply {
                    Log.d("debug2", "MEMBER")
                    swipeRefresh.visibility = View.VISIBLE
                    layoutNonMember.visibility = View.GONE
                    loadingOverlay.visibility = View.GONE
                    layoutPending.visibility = View.GONE
                }
            }
            UserRole.PENDINGMEMBER -> {
                binding.apply {
                    Log.d("debug2", "PENDING MEMBER")
                    swipeRefresh.visibility = View.GONE
                    layoutNonMember.visibility = View.GONE
                    loadingOverlay.visibility = View.GONE
                    layoutPending.visibility = View.VISIBLE
                    btnRefreshPending.setOnClickListener {
                        viewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
                            val intent = Intent(requireContext(), ProfileDetailMembershipkuActivity::class.java).apply {
                                putExtra(ProfileDetailMembershipkuActivity.EXTRA_USER_ID, loginDomain.user.id)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
            UserRole.NONMEMBER -> {
                binding.apply {
                    Log.d("debug2", "NMEMBER")
                    swipeRefresh.visibility = View.GONE
                    loadingOverlay.visibility = View.GONE
                    layoutPending.visibility = View.GONE

                    // Check if layoutNonMember exists in this layout
                    layoutNonMember.visibility = View.VISIBLE
                    btnDaftar.setOnClickListener {
                        val intent = Intent(requireContext(), HomeMemberLevelActivity::class.java).apply {
                            putExtra(HomeMemberLevelActivity.EXTRA_USER_ID, userId)
                        }
                        startActivity(intent)
                    }
                }
            }
            else -> {
                binding.apply {
                    swipeRefresh.visibility = View.GONE
                    loadingOverlay.visibility = View.GONE
                    layoutNonMember.visibility = View.GONE
                }
            }
        }
    }

    private fun setupSwipeRefresh(userId: String) {
        binding.swipeRefresh.setOnRefreshListener {
            // Don't show the loading overlay, only use the SwipeRefresh indicator
            // Reload data
            viewModel.getUserHistory(userId)

            // The swipe refresh indicator will be dismissed in observePointHistory()
        }
    }

    private fun observePointHistory() {
        lifecycleScope.launch {
            viewModel.userHistory.collect { resource ->
                // Always manage the SwipeRefresh indicator state
                binding.swipeRefresh.isRefreshing = false

                when (resource) {
                    is Resource.Loading -> {
                        // Only show loading overlay if this was NOT triggered by SwipeRefresh
                        if (!binding.swipeRefresh.isRefreshing) {
                            showLoading(true)
                        }
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        resource.data?.let { history ->
                            if (history.history.isEmpty()) {
                                showEmptyState()
                            } else {
                                binding.rvPoin.visibility = View.VISIBLE
                                binding.tvTidakAdaRiwayat.visibility = View.GONE
                                historyAdapter.updateItems(history.history)
                            }
                        }
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        showEmptyState()
                    }
                    else -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(userId: String) {
        historyAdapter = DashboardPointHistoryAdapter(userId)
        binding.rvPoin.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvPoin.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showEmptyState() {
        // Check if the empty state TextView exists in this layout
        binding.tvTidakAdaRiwayat.visibility = View.VISIBLE
        binding.rvPoin.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}