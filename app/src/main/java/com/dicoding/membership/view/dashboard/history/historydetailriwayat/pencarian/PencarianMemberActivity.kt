package com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.membership.databinding.ActivityPencarianPoinBinding
import com.dicoding.membership.databinding.FilterPencarianPoinBinding
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.adapter.CategoryFilterAdapter
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.adapter.DateFilterAdapter
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.adapter.SearchPointHistoryAdapter
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.dataclass.CategoryFilter
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.dataclass.DateFilter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class PencarianPoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPencarianPoinBinding
    private val viewModel: PencarianPoinViewModel by viewModels()
    private lateinit var historyAdapter: SearchPointHistoryAdapter

    private var selectedDateFilter: DateFilter? = null
    private var selectedCategoryFilter: CategoryFilter? = null
    private var originalList: List<PointHistory> = listOf()
    private var currentUserId: String = ""
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPencarianPoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupOnClickListener()
        setupSearchView()
    }

    private fun setupSearchView() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        viewModel.getUserData()

        viewModel.userData.observe(this) { loginDomain ->
            loginDomain?.let { data ->
                currentUserId = data.user.id
                setupRecyclerView(data.user.id)
                viewModel.getUserHistory(data.user.id)
            }
        }

        lifecycleScope.launch {
            viewModel.userHistory.collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Success -> {
                        showLoading(false)
                        resource.data?.let { history ->
                            originalList = history.history
                            applyFilters()
                        }
                    }
                    is Resource.Error -> showLoading(false)
                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerView(userId: String) {
        historyAdapter = SearchPointHistoryAdapter(userId)
        binding.rvPoin.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@PencarianPoinActivity)
        }
    }

    private fun applyFilters() {
        var filteredList = originalList

        // Apply search filter
        if (searchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { history ->
                val otherUserName = if (history.from.id == currentUserId) {
                    history.to.name
                } else {
                    history.from.name
                }
                otherUserName.contains(searchQuery, ignoreCase = true)
            }
        }

        // Apply category filter
        if (selectedCategoryFilter != null) {
            filteredList = filteredList.filter { history ->
                when (selectedCategoryFilter) {
                    CategoryFilter.TRANSFER -> history.from.id == currentUserId
                    CategoryFilter.RECEIVE -> history.to.id == currentUserId
                    null -> true
                }
            }
        }

        // Apply date filter
        if (selectedDateFilter != null) {
            val calendar = Calendar.getInstance()

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            filteredList = filteredList.filter { history ->
                val transactionDate = inputFormat.parse(history.createdAt) ?: return@filter false
                val transactionCalendar = Calendar.getInstance().apply { time = transactionDate }

                when (selectedDateFilter) {
                    DateFilter.TODAY -> {
                        calendar.get(Calendar.YEAR) == transactionCalendar.get(Calendar.YEAR) &&
                                calendar.get(Calendar.DAY_OF_YEAR) == transactionCalendar.get(Calendar.DAY_OF_YEAR)
                    }
                    DateFilter.THIS_MONTH -> {
                        calendar.get(Calendar.YEAR) == transactionCalendar.get(Calendar.YEAR) &&
                                calendar.get(Calendar.MONTH) == transactionCalendar.get(Calendar.MONTH)
                    }
                    DateFilter.THIS_YEAR -> {
                        calendar.get(Calendar.YEAR) == transactionCalendar.get(Calendar.YEAR)
                    }
                    null -> true
                }
            }
        }

        // Update UI based on results
        if (filteredList.isEmpty()) {
            binding.tvTidakAdaRiwayat.visibility = View.VISIBLE
            binding.rvPoin.visibility = View.GONE
        } else {
            binding.tvTidakAdaRiwayat.visibility = View.GONE
            binding.rvPoin.visibility = View.VISIBLE
            historyAdapter.updateItems(filteredList)
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val filterBinding = FilterPencarianPoinBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialog.setContentView(filterBinding.root)

        // Setup date filter with current selection
        val dateFilterAdapter = DateFilterAdapter(
            selectedFilter = selectedDateFilter
        ) { dateFilter ->
            selectedDateFilter = if (selectedDateFilter == dateFilter) null else dateFilter
            applyFilters()
        }
        filterBinding.filterRecyclerview1.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateFilterAdapter
        }

        // Setup category filter with current selection
        val categoryFilterAdapter = CategoryFilterAdapter(
            selectedFilter = selectedCategoryFilter
        ) { categoryFilter ->
            selectedCategoryFilter = if (selectedCategoryFilter == categoryFilter) null else categoryFilter
            applyFilters()
        }
        filterBinding.filterRecyclerview2.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryFilterAdapter
        }

        bottomSheetDialog.show()
    }

    private fun setupOnClickListener() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvPoin.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}