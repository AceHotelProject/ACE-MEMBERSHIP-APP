package com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearchfilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PromoFilterBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    private val viewModel: PromoFilterBottomSheetViewModel by viewModels()

    private lateinit var dateAdapter: FilterChipAdapter
    private lateinit var statusAdapter: FilterChipAdapter
    private lateinit var categoryAdapter: FilterChipAdapter

    private val dates = listOf("All", "Hari Ini", "Bulan Ini", "Tahun Ini")
    private val statuses = listOf("All", "active", "redeemed")
    private val categories = listOf(
        "All", "Hotel", "Penginapan", "Market", "Restoran",
        "Hiburan", "Sekolah", "Kesehatan", "Pariwisata", "Gym"
    )

    private var onFilterSelected: ((FilterType, String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserRole()
    }

    private fun checkUserRole() {
        viewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)
            //            Testing
            val mockUserRole = UserRole.MEMBER

            setupFiltersByRole(mockUserRole)
        }
    }

    private fun setupFiltersByRole(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.MITRA -> {
                // Show all filters for staff roles
                binding.apply {
                    // Show date filter section
                    tvDateFilter.visibility = View.VISIBLE
                    filterRecyclerviewDate.visibility = View.VISIBLE

                    // Show status filter section
                    tvStatusFilter.visibility = View.VISIBLE
                    filterRecyclerviewStatus.visibility = View.VISIBLE

                    // Show category filter section
                    tvCategoryFilter.visibility = View.VISIBLE
                    filterRecyclerviewCategory.visibility = View.VISIBLE
                }
                setupRecyclerViews(showStatusFilter = true)
            }

            UserRole.MEMBER, UserRole.NONMEMBER -> {
                // Show limited filters for non-staff roles
                binding.apply {
                    // Show date filter section
                    tvDateFilter.visibility = View.VISIBLE
                    filterRecyclerviewDate.visibility = View.VISIBLE

                    // Hide status filter section
                    tvStatusFilter.visibility = View.GONE
                    filterRecyclerviewStatus.visibility = View.GONE

                    // Show category filter section
                    tvCategoryFilter.visibility = View.VISIBLE
                    filterRecyclerviewCategory.visibility = View.VISIBLE
                }
                setupRecyclerViews(showStatusFilter = false)
            }
            UserRole.UNDEFINED -> {
                // Handle undefined role - bisa menggunakan default behavior seperti NONMEMBER
                binding.apply {
                    tvDateFilter.visibility = View.GONE
                    filterRecyclerviewDate.visibility = View.GONE
                    tvStatusFilter.visibility = View.GONE
                    filterRecyclerviewStatus.visibility = View.GONE
                    tvCategoryFilter.visibility = View.GONE
                    filterRecyclerviewCategory.visibility = View.GONE
                }
                setupRecyclerViews(showStatusFilter = false)
            }
        }
    }


    private fun setupRecyclerViews(showStatusFilter: Boolean) {
        setupDateFilter()
        if (showStatusFilter) {
            setupStatusFilter()
        }
        setupCategoryFilter()
    }

    private fun setupDateFilter() {
        dateAdapter = FilterChipAdapter { selectedDate ->
            onFilterSelected?.invoke(FilterType.DATE, selectedDate)
            // Simpan posisi saat item dipilih
            viewModel.setDatePosition(dateAdapter.getSelectedPosition())
        }
        binding.filterRecyclerviewDate.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter
            dateAdapter.submitList(dates)
        }

        // Terapkan posisi yang tersimpan setelah list di-submit
        viewModel.selectedDatePosition.value.let { position ->
            dateAdapter.setSelectedPosition(position)
        }
    }

    private fun setupStatusFilter() {
        statusAdapter = FilterChipAdapter { selectedStatus ->
            onFilterSelected?.invoke(FilterType.STATUS, selectedStatus)
            // Simpan posisi saat item dipilih
            viewModel.setStatusPosition(statusAdapter.getSelectedPosition())
        }
        binding.filterRecyclerviewStatus.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = statusAdapter
            statusAdapter.submitList(statuses)
        }

        // Terapkan posisi yang tersimpan setelah list di-submit
        viewModel.selectedStatusPosition.value.let { position ->
            statusAdapter.setSelectedPosition(position)
        }
    }

    private fun setupCategoryFilter() {
        categoryAdapter = FilterChipAdapter { selectedCategory ->
            onFilterSelected?.invoke(FilterType.CATEGORY, selectedCategory)
            // Simpan posisi saat item dipilih
            viewModel.setCategoryPosition(categoryAdapter.getSelectedPosition())
        }
        binding.filterRecyclerviewCategory.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            categoryAdapter.submitList(categories)
        }

        // Terapkan posisi yang tersimpan setelah list di-submit
        viewModel.selectedCategoryPosition.value.let { position ->
            categoryAdapter.setSelectedPosition(position)
        }
    }

    fun setOnFilterSelectedListener(listener: (FilterType, String) -> Unit) {
        onFilterSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class FilterType {
        DATE, STATUS, CATEGORY
    }

    companion object {
        const val TAG = "FilterBottomSheetDialog"
    }
}