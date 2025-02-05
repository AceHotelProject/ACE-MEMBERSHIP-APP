package com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearchfilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.BottomSheetFilterBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoFilterBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    private val viewModel: PromoFilterBottomSheetViewModel by viewModels()

    private lateinit var dateAdapter: FilterChipAdapter
    private lateinit var statusAdapter: FilterChipAdapter
    private lateinit var categoryAdapter: FilterChipAdapter

    private val dates = listOf("Semua", "Hari Ini", "Bulan Ini", "Tahun Ini")
    private val statuses = listOf("Semua", "active", "redeemed")
    private val categories = listOf(
        "Semua", "Hotel", "Penginapan", "Market", "Restoran",
        "Hiburan", "Sekolah", "Kesehatan", "Pariwisata", "Gym"
    )

    private var onFilterSelected: ((FilterType, String) -> Unit)? = null

    private var isFromHistory = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        isFromHistory = arguments?.getBoolean("isFromHistory") ?: false
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
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.apply {
                    if (isFromHistory) {
                        tvDateFilter.visibility = View.VISIBLE
                        filterRecyclerviewDate.visibility = View.VISIBLE

                        // valid (Admin, Mitra) & draft (Receptionist)
                        tvStatusFilter.visibility = View.GONE
                        filterRecyclerviewStatus.visibility = View.GONE

                        tvCategoryFilter.visibility = View.GONE
                        filterRecyclerviewCategory.visibility = View.GONE
                    } else {
                        tvDateFilter.visibility = View.GONE
                        filterRecyclerviewDate.visibility = View.GONE

                        // Status valid
                        tvStatusFilter.visibility = View.GONE
                        filterRecyclerviewStatus.visibility = View.GONE

                        tvCategoryFilter.visibility = View.VISIBLE
                        filterRecyclerviewCategory.visibility = View.VISIBLE
                    }
                }
                setupRecyclerViews(showStatusFilter = true)
            }

            UserRole.MEMBER, UserRole.NONMEMBER -> {
                binding.apply {
                    if (isFromHistory) {
                        tvDateFilter.visibility = View.VISIBLE
                        filterRecyclerviewDate.visibility = View.VISIBLE

                        // redeemed (member) & no status (nonmember)
                        tvStatusFilter.visibility = View.GONE
                        filterRecyclerviewStatus.visibility = View.GONE

                        tvCategoryFilter.visibility = View.VISIBLE
                        filterRecyclerviewCategory.visibility = View.VISIBLE
                    } else {
                        tvDateFilter.visibility = View.GONE
                        filterRecyclerviewDate.visibility = View.GONE

                        // Status valid
                        tvStatusFilter.visibility = View.GONE
                        filterRecyclerviewStatus.visibility = View.GONE

                        tvCategoryFilter.visibility = View.VISIBLE
                        filterRecyclerviewCategory.visibility = View.VISIBLE
                    }
                }
                setupRecyclerViews(showStatusFilter = false)
            }
            UserRole.UNDEFINED -> {
                // Same as Member & NonMember
                binding.apply {
                    if (isFromHistory) {
                        tvDateFilter.visibility = View.VISIBLE
                        filterRecyclerviewDate.visibility = View.VISIBLE

                        // redeemed (member) & no status (nonmember)
                        tvStatusFilter.visibility = View.GONE
                        filterRecyclerviewStatus.visibility = View.GONE

                        tvCategoryFilter.visibility = View.VISIBLE
                        filterRecyclerviewCategory.visibility = View.VISIBLE
                    } else {
                        tvDateFilter.visibility = View.GONE
                        filterRecyclerviewDate.visibility = View.GONE

                        // Status valid
                        tvStatusFilter.visibility = View.GONE
                        filterRecyclerviewStatus.visibility = View.GONE

                        tvCategoryFilter.visibility = View.VISIBLE
                        filterRecyclerviewCategory.visibility = View.VISIBLE
                    }
                }
                setupRecyclerViews(showStatusFilter = false)
            }
            UserRole.USER -> {
                binding.apply {
                    if (isFromHistory) {
                        tvDateFilter.visibility = View.VISIBLE
                        filterRecyclerviewDate.visibility = View.VISIBLE

                        // redeemed (member) & no status (nonmember)
                        tvStatusFilter.visibility = View.GONE
                        filterRecyclerviewStatus.visibility = View.GONE

                        tvCategoryFilter.visibility = View.VISIBLE
                        filterRecyclerviewCategory.visibility = View.VISIBLE
                    } else {
                        tvDateFilter.visibility = View.GONE
                        filterRecyclerviewDate.visibility = View.GONE

                        // Status valid
                        tvStatusFilter.visibility = View.GONE
                        filterRecyclerviewStatus.visibility = View.GONE

                        tvCategoryFilter.visibility = View.VISIBLE
                        filterRecyclerviewCategory.visibility = View.VISIBLE
                    }
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
            viewModel.setDatePosition(dateAdapter.getSelectedPosition())
        }
        binding.filterRecyclerviewDate.apply {
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            adapter = dateAdapter
            dateAdapter.submitList(dates)
        }
        viewModel.selectedDatePosition.value.let { position ->
            dateAdapter.setSelectedPosition(position)
        }
    }

    private fun setupStatusFilter() {
        statusAdapter = FilterChipAdapter { selectedStatus ->
            onFilterSelected?.invoke(FilterType.STATUS, selectedStatus)
            viewModel.setStatusPosition(statusAdapter.getSelectedPosition())
        }
        binding.filterRecyclerviewStatus.apply {
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            adapter = statusAdapter
            statusAdapter.submitList(statuses)
        }
        viewModel.selectedStatusPosition.value.let { position ->
            statusAdapter.setSelectedPosition(position)
        }
    }

    private fun setupCategoryFilter() {
        categoryAdapter = FilterChipAdapter { selectedCategory ->
            onFilterSelected?.invoke(FilterType.CATEGORY, selectedCategory)
            viewModel.setCategoryPosition(categoryAdapter.getSelectedPosition())
        }
        binding.filterRecyclerviewCategory.apply {
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            adapter = categoryAdapter
            categoryAdapter.submitList(categories)
        }
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