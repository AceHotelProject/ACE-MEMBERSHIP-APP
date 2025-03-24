package com.dicoding.membership.view.dashboard.member

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.databinding.FragmentMemberBinding
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.HistoryDetailRiwayatActivity
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.PencarianMemberActivity
import com.dicoding.membership.view.dashboard.member.listeditmember.ListEditMemberActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemberFragment : Fragment() {

    private var _binding: FragmentMemberBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter

    private val memberViewModel: MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView implementation
        setupRecyclerView()

        // Setup SwipeRefreshLayout
        setupSwipeRefresh()

        // Observe data
        observeUserData()

        // Set default member filter to true and load data
        memberViewModel.getAllUsers()

        validateToken()
        handleMenuButton()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // Reset data and reload
            memberViewModel.currentPage = 1
            memberViewModel.isLastPage = false
            memberViewModel.getAllUsers()
        }
    }

    private fun observeUserData() {
        memberViewModel.userList.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    // Always stop refreshing and hide loading when success
                    binding.swipeRefresh.isRefreshing = false
                    showLoading(false)

                    resource.data?.let { userList ->
                        if (memberViewModel.currentPage == 1) {
                            userAdapter.setData(userList.data)
                        } else {
                            userAdapter.addData(userList.data)
                        }

                        // Update membership stats
                        updateMembershipStats(userList.data)
                    }
                }
                is Resource.Error -> {
                    // Always stop refreshing and hide loading when error
                    binding.swipeRefresh.isRefreshing = false
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.swipeRefresh.isRefreshing = false
                    showLoading(false)
                }
            }
        }
    }

    private fun updateMembershipStats(users: List<User>) {
        val totalMembers = users.size

        // Group by subscription type and count
        val membershipCounts = users
            .mapNotNull { it.membership?.subscriptionType?.type }
            .groupingBy { it }
            .eachCount()

        // Set total members
        binding.tvTotalMember.text = totalMembers.toString()

        // Get top 3 membership types by count
        val topMemberships = membershipCounts
            .entries
            .sortedByDescending { it.value }
            .take(3)
            .toList()

        // Fill the stats boxes
        if (topMemberships.isNotEmpty()) {
            binding.tvMember2.text = "Member ${topMemberships[0].key}"
            binding.tvMember2Count.text = topMemberships[0].value.toString()
        }

        if (topMemberships.size > 1) {
            binding.tvMember3.text = "Member ${topMemberships[1].key}"
            binding.tvMember3Count.text = topMemberships[1].value.toString()
        }

        if (topMemberships.size > 2) {
            binding.tvMember4.text = "Member ${topMemberships[2].key}"
            binding.tvMember4Count.text = topMemberships[2].value.toString()
        }
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter().apply {
            setOnItemClickListener { userId ->
                val intent = Intent(requireContext(), HistoryDetailRiwayatActivity::class.java).apply {
                    putExtra(HistoryDetailRiwayatActivity.EXTRA_USER_ID, userId)
                }
                startActivity(intent)
            }
        }

        binding.listMemberRecyclerview.apply {
            setHasFixedSize(false)
            setItemViewCacheSize(20)
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            layoutManager = LinearLayoutManager(context).apply {
                isAutoMeasureEnabled = true
            }
            adapter = userAdapter
        }

        // Update scroll listener for pagination
        binding.svBody.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) { // Scrolling down
                val bottomReached = scrollY + v.height >= v.getChildAt(0).height - 150 // 150dp threshold
                if (bottomReached) {
                    memberViewModel.loadMoreUsers()
                }
            }
        })
    }

    private fun validateToken() {
        memberViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun handleMenuButton() {
        binding.layoutEditMembership.setOnClickListener {
            val intent = Intent(requireActivity(), ListEditMemberActivity::class.java)
            startActivity(intent)
        }
        binding.btnSearch.setOnClickListener {
            val intent = Intent(requireActivity(), PencarianMemberActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(boolean: Boolean){
        binding.loadingOverlay.visibility = if(boolean) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}