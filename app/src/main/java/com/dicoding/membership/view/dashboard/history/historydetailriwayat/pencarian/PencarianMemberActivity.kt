package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.databinding.ActivityPencarianMemberBinding
import com.dicoding.membership.databinding.FilterPencarianMemberBinding
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.HistoryDetailRiwayatActivity
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter.DateFilter
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter.DateFilterAdapter
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter.MemberTypeFilterAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PencarianMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPencarianMemberBinding
    private val viewModel: PencarianMemberViewModel by viewModels()
    private lateinit var userAdapter: UserSearchAdapter

    // Filter states
    private var selectedDateFilter: DateFilter? = null
    private var selectedMemberType: String? = null
    private var currentSearchQuery: String? = null

    // Search debounce job
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPencarianMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupOnClickListener()
        setupSearchView()

        // Initial data load with no filters
        showLoading(true) // Show loading indicator for initial load
        viewModel.searchUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserSearchAdapter()
        binding.rvPoin.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@PencarianMemberActivity)
        }

        userAdapter.setOnItemClickListener { userId ->
            val intent = Intent(this, HistoryDetailRiwayatActivity::class.java).apply {
                putExtra(HistoryDetailRiwayatActivity.EXTRA_USER_ID, userId)
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
                    showLoading(false)
                    resource.data?.let { userList ->
                        if (viewModel.currentPage == 1) {
                            userAdapter.setData(userList.data)
                        }

                        updateEmptyState(userList.data.isEmpty())
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, resource.message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()

                    // Clear adapter data and show empty state
                    userAdapter.clearData()
                    updateEmptyState(true)
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

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.tvTidakAdaRiwayat.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvPoin.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun setupSearchView() {
        // Handle keyboard search action
        binding.searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Hide keyboard
                hideKeyboard()

                // Get search query and perform search
                currentSearchQuery = binding.searchEditText.text.toString().takeIf { it.isNotEmpty() }

                // Show loading and search
                showLoading(true)
                applyFilters()
                return@setOnEditorActionListener true
            }
            false
        }

        // Add text change watcher (optional, keeping it for live search functionality)
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Cancel previous search job
                searchJob?.cancel()

                // Start new search job with debounce
                searchJob = MainScope().launch {
                    delay(800) // Longer debounce for typing (800ms)
                    currentSearchQuery = s?.toString()?.takeIf { it.isNotEmpty() }

                    // Show loading indicator before searching
                    showLoading(true)
                    applyFilters()
                }
            }
        })
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
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
        }
        filterBinding.filterTanggal.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateFilterAdapter
        }

        // Hide the kategori section
        filterBinding.filterKategori.visibility = View.GONE
        // Try different approaches to find and hide the label
        try {
            val kategoriLabelId = resources.getIdentifier("kategori_label", "id", packageName)
            if (kategoriLabelId != 0) {
                filterBinding.root.findViewById<View>(kategoriLabelId)?.visibility = View.GONE
            }
        } catch (e: Exception) {
            // Ignore if the ID doesn't exist
        }

        // Setup member type filter
        val memberTypeAdapter = MemberTypeFilterAdapter(
            selectedFilter = selectedMemberType
        ) { memberType ->
            selectedMemberType = if (selectedMemberType == memberType) null else memberType
        }
        filterBinding.filterTipeMember.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = memberTypeAdapter
        }

        // Set member types data from ViewModel
        viewModel.membershipTypes.value?.let { types ->
            memberTypeAdapter.setData(types)
        }

        // Add apply filter button click listener
        filterBinding.btnApplyFilter?.setOnClickListener {
            // Dismiss dialog first
            bottomSheetDialog.dismiss()

            // IMPORTANT: Show loading before applying filters
            showLoading(true)

            // Apply filters with a slight delay to ensure loading is visible
            MainScope().launch {
                delay(100) // Small delay to ensure UI updates
                applyFilters()
            }
        }

        // Add reset filter button
        filterBinding.btnResetFilter?.setOnClickListener {
            selectedDateFilter = null
            selectedMemberType = null

            try {
                // Use extension functions if available in the adapters
                dateFilterAdapter.resetSelection()
                memberTypeAdapter.resetSelection()
            } catch (e: Exception) {
                // If reset methods don't exist, just notify data changed
                dateFilterAdapter.notifyDataSetChanged()
                memberTypeAdapter.notifyDataSetChanged()
            }
        }

        bottomSheetDialog.show()
    }

    private fun applyFilters() {
        // Convert DateFilter to API parameter
        val startDateParam = when (selectedDateFilter) {
            DateFilter.TODAY -> "today"
            DateFilter.THIS_MONTH -> "this_month"
            DateFilter.THIS_YEAR -> "this_year"
            else -> null
        }

        // Reset to page 1 and search with new filters
        viewModel.searchUsers(
            search = currentSearchQuery,
            startDate = startDateParam,
            subscriptionType = selectedMemberType
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Always hide results during loading
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvPoin.visibility = View.GONE
            binding.tvTidakAdaRiwayat.visibility = View.GONE
        }
    }
}