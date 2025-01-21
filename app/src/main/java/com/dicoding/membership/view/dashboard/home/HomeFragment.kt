package com.dicoding.membership.view.dashboard.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.databinding.FragmentHomeBinding
import com.dicoding.membership.view.dashboard.promo.PromoAdapter
import com.dicoding.membership.view.dashboard.promo.PromoFragment
import com.dicoding.membership.view.dashboard.promo.detail.detailpromo.PromoDetailActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var promoAdapter: PromoAdapter

    private var isDataInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPromoRecyclerView()

        setupSwipeRefresh()

        // Hanya load data jika belum diinisialisasi
        if (!isDataInitialized) {
            validateToken()

            checkUserRole()

            observePromos()

            isDataInitialized = true
        }
    }

    private fun setupPromoRecyclerView() {
        promoAdapter = PromoAdapter()
        binding.rvPromo.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = promoAdapter
        }

        // Set click callback untuk navigasi ke detail
        promoAdapter.setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
            override fun onItemClicked(data: PromoDomain) {
                navigateToDetail(data)
            }
        })
    }

    private fun navigateToDetail(promo: PromoDomain) {
        val intent = Intent(requireActivity(), PromoDetailActivity::class.java).apply {
            putExtra(PromoDetailActivity.EXTRA_PROMO, promo)
            putExtra(PromoDetailActivity.EXTRA_SOURCE, PromoFragment.PROMO_SOURCE_MITRA) // Atau sesuaikan dengan source yang sesuai
        }
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // Reset state dan muat ulang data
            isDataInitialized = false
            promoAdapter.refresh()
            validateToken()
            checkUserRole()
            observePromos()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun observePromos() {
        // Tracking state untuk kedua loading
        var isPagingLoading = false
        var isProposalLoading = false

        fun updateLoadingState() {
            // Tampilkan loading jika salah satu atau keduanya masih loading
            showLoading(isPagingLoading || isProposalLoading)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.getPromos().collectLatest { pagingData ->
                    Log.d("HomeFragment", "Received paging data")
                    // Filter paging data
                    val filteredPagingData = pagingData.filter {
                        Log.d("HomeFragment", "Checking promo: ${it.name}, isActive: ${it.isActive}")
                        it.status == "active"
                    }
                    Log.d("HomeFragment", "Submitting filtered paging data to mitra adapter")
                    promoAdapter.submitData(filteredPagingData)
                    // You can also observe the paging state here
                    promoAdapter.addLoadStateListener { loadState ->
                        when (loadState.refresh) {
                            is LoadState.Loading -> {
                                Log.d("HomeFragment", "Paging Loading")
                            }
                            is LoadState.Error -> {
                                Log.e("HomeFragment", "Paging Error")
                            }
                            is LoadState.NotLoading -> {
                                Log.d("HomeFragment", "Paging Not Loading")
                            }
                        }
                    }
                }
            }
        }

        // Handle proposal promos loading state
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.getProposalPromos().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isProposalLoading = true
                        updateLoadingState()
                    }
                    is Resource.Success -> {
                        isProposalLoading = false
                        updateLoadingState()
                        result.data?.let { promoData ->
                            val inactiveCount = promoData.results.filter { it.status == "active" }.size
                            binding.tvCouponCount.apply {
                                text = "$inactiveCount"
                                visibility = if (inactiveCount > 0) View.VISIBLE else View.GONE
                            }
                        }
                    }
                    is Resource.Error -> {
                        isProposalLoading = false
                        updateLoadingState()
                        binding.tvCouponCount.visibility = View.GONE
                        Log.e("HomeFragment", "Error getting coupon count: ${result.message}")
                    }
                    else -> {
                        isProposalLoading = false
                        updateLoadingState()
                    }
                }
            }

            // Handle paging loading state
            promoAdapter.loadStateFlow.collect { loadState ->
                // Update paging loading state
                isPagingLoading = loadState.source.refresh is LoadState.Loading
                updateLoadingState()

                // Handle empty state
                val isEmpty = loadState.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        promoAdapter.itemCount == 0

                binding.tvEmptyPromo.isVisible = isEmpty
                binding.rvPromo.isVisible = !isEmpty

                // Handle error state
                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan: ${it.error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun validateToken() {
        homeViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        homeViewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.MEMBER
            setupLinearVisibility(mockUserRole)

//            Use This For Real
//            setupFabVisibility(userRole)

            Log.d("HomeFragment", "User Role: ${userRole.display}")
        }
    }

    private fun setupLinearVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.linearLayoutMember.visibility = View.GONE
                binding.linearLayoutNonMember.visibility = View.GONE
            }
            UserRole.MEMBER -> {
                binding.linearLayoutMember.visibility = View.VISIBLE
                binding.linearLayoutNonMember.visibility = View.GONE
            }
            UserRole.NONMEMBER -> {
                binding.linearLayoutMember.visibility = View.GONE
                binding.linearLayoutNonMember.visibility = View.VISIBLE
            }
            else -> {
                binding.linearLayoutMember.visibility = View.GONE
                binding.linearLayoutNonMember.visibility = View.GONE
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}