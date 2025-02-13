package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.membership.databinding.ActivityPencarianMemberBinding
import com.dicoding.membership.databinding.ActivityPencarianPoinBinding
import com.dicoding.membership.databinding.FilterPencarianMemberBinding
import com.dicoding.membership.databinding.FilterPencarianPoinBinding
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.adapter.CategoryFilterAdapter
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.adapter.DateFilterAdapter
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.adapter.SearchPointHistoryAdapter
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.dataclass.CategoryFilter
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.dataclass.DateFilter
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.HistoryDetailRiwayatActivity
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter.MemberTypeFilterAdapter
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter.MembershipStatus
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter.StatusFilterAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PencarianMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPencarianMemberBinding
    private val viewModel: PencarianMemberViewModel by viewModels()
    private lateinit var memberAdapter: MemberSearchAdapter

    // Filter states
    private var selectedDateFilter: DateFilter? = null
    private var selectedStatusFilter: MembershipStatus? = null
    private var selectedMemberType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPencarianMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupOnClickListener()
        setupSearchView()

        // Initial data load
        viewModel.loadAllData()

    }

    private fun setupRecyclerView() {
        memberAdapter = MemberSearchAdapter()
        binding.rvPoin.apply {
            adapter = memberAdapter
            layoutManager = LinearLayoutManager(this@PencarianMemberActivity)
        }

        memberAdapter.setOnItemClickListener { user ->
            // Start HistoryDetailRiwayatActivity with the selected user's ID
            val intent = Intent(this, HistoryDetailRiwayatActivity::class.java).apply {
                putExtra(HistoryDetailRiwayatActivity.EXTRA_USER_ID, user.id)
            }
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.userList.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    resource.data?.let { userList ->
                        memberAdapter.addData(userList.data)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, resource.message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }

        viewModel.isLoadingComplete.observe(this) { isComplete ->
            if (isComplete) {
                showLoading(false)
                // Enable search and filters
                binding.searchEditText.isEnabled = true
                binding.btnFilter.isEnabled = true
            }
        }
    }

    private fun setupSearchView() {
        binding.searchEditText.isEnabled = false // Disable until loading complete
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                applyFilters(s?.toString())
            }
        })
    }

    private fun setupOnClickListener() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val filterBinding = FilterPencarianMemberBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(filterBinding.root)

        // Setup date filter
        val dateFilterAdapter = DateFilterAdapter(
            selectedFilter = selectedDateFilter
        ) { dateFilter ->
            selectedDateFilter = if (selectedDateFilter == dateFilter) null else dateFilter
            applyFilters()
        }
        filterBinding.filterTanggal.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateFilterAdapter
        }

        // Setup status filter
        val statusFilterAdapter = StatusFilterAdapter(
            selectedFilter = selectedStatusFilter
        ) { statusFilter ->
            selectedStatusFilter = if (selectedStatusFilter == statusFilter) null else statusFilter
            applyFilters()
        }
        filterBinding.filterKategori.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = statusFilterAdapter
        }

        // Setup member type filter
        val memberTypeAdapter = MemberTypeFilterAdapter(
            selectedFilter = selectedMemberType
        ) { memberType ->
            selectedMemberType = if (selectedMemberType == memberType) null else memberType
            applyFilters()
        }
        filterBinding.filterTipeMember.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = memberTypeAdapter
        }

        // Set member types data from ViewModel
        viewModel.membershipTypes.value?.let { types ->
            memberTypeAdapter.setData(types)
        }

        bottomSheetDialog.show()
    }

    private fun applyFilters(searchQuery: String? = null) {
        memberAdapter.applyFilter { user ->
            var matches = true

            // Apply search
            searchQuery?.let { query ->
                if (query.isNotEmpty()) {
                    matches = matches && (
                            user.name.contains(query, ignoreCase = true) ||
                                    user.email.contains(query, ignoreCase = true) ||
                                    (user.phone?.contains(query, ignoreCase = true) ?: false)
                            )
                }
            }

            // Apply other filters
            selectedDateFilter?.let { dateFilter ->
                // Your date filter logic
            }

            selectedStatusFilter?.let { statusFilter ->
                // Your status filter logic
            }

            selectedMemberType?.let { memberType ->
                matches = matches && user.memberType == memberType
            }

            matches
        }

        // Show/hide empty state
        binding.tvTidakAdaRiwayat.visibility =
            if (memberAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvPoin.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}