package com.dicoding.membership.view.dashboard.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
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

        binding.rvNMPromo.apply {
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
            putExtra(PromoDetailActivity.EXTRA_PROMO, data)
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
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe LoadState dari adapter
            promoAdapter.loadStateFlow.collectLatest { loadState ->
                when (loadState.refresh) {
                    is LoadState.Loading -> {
                        showLoading(true)
                        Log.d("HomeFragment", "Promo Loading")
                    }
                    is LoadState.NotLoading -> {
                        showLoading(false)
                        Log.d("HomeFragment", "Promo Ready")

                        // Update coupon count
                        val validCoupons = promoAdapter.snapshot().items.count { it.status == "valid" }
                        binding.tvCouponCount.apply {
                            text = "$validCoupons"
                            visibility = if (validCoupons > 0) View.VISIBLE else View.GONE
                        }
                    }
                    is LoadState.Error -> {
                        showLoading(false)
                        // Handle error jika diperlukan
                        Log.e("HomeFragment", "Promo Error: ${(loadState.refresh as LoadState.Error).error.message}")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.getPromos(
                    category = "",
                    status = "valid",
                    name = ""
                ).collectLatest { pagingData ->
                    Log.d("HomeFragment", "Received paging data")
                    promoAdapter.submitData(pagingData)
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
            val mockUserRole = UserRole.NONMEMBER
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