package com.dicoding.membership.view.dashboard.promo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.databinding.FragmentPromoBinding
import com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearch.PromoSearchActivity
import com.dicoding.membership.view.dashboard.promo.detail.detailpromo.PromoDetailActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PromoFragment : Fragment() {

    private var _binding: FragmentPromoBinding? = null
    private val viewModel: PromoViewModel by viewModels()
    private val binding get() = _binding!!

    private lateinit var ajuanPromoAdapter: PromoAdapter
    private lateinit var promoMitraAdapter: PromoAdapter

    private lateinit var promoCategoryAdapter: PromoCategoryAdapter

    private val promoViewModel: PromoViewModel by viewModels()
//    private lateinit var storyPagingAdapter: StoryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeRefresh()

        validateToken()

        checkUserRole()

        setupRecyclerViews()

        observePromos()

//        setupRecyclerView()
//
//        lifecycleScope.launch {
//            promoViewModel.getStories("default_filter", false).collectLatest { pagingData ->
//                storyPagingAdapter.submitData(pagingData)
//            }
//        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            ajuanPromoAdapter.refresh()
            promoMitraAdapter.refresh()
        }
    }

    private fun setupRecyclerViews() {
        // Setup Ajuan Promo adapter
        ajuanPromoAdapter = PromoAdapter().apply {
            setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
                override fun onItemClicked(data: PromoDomain) {
                    navigateToDetail(data, PROMO_SOURCE_AJUAN)
                }
            })
        }

        // Setup Promo Mitra adapter
        promoMitraAdapter = PromoAdapter().apply {
            setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
                override fun onItemClicked(data: PromoDomain) {
                    navigateToDetail(data, PROMO_SOURCE_MITRA)
                }
            })
        }

        // Attach adapters to RecyclerViews
        binding.rvAjuanPromo.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,  // Set orientation to horizontal
                false
            )
            adapter = ajuanPromoAdapter
        }

        binding.rvPromoMitra.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = promoMitraAdapter
        }

        binding.buttonDashboardPromoSearch.setOnClickListener {
            val intent = Intent(requireActivity(), PromoSearchActivity::class.java)
            startActivity(intent)
        }

        promoCategoryAdapter = PromoCategoryAdapter { category ->
            Log.d("PromoFragment", "Category selected: $category")
            viewModel.setCategory(category)
            promoMitraAdapter.refresh()
        }

        binding.rvPromoCategory.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = promoCategoryAdapter
        }
    }

    private fun navigateToDetail(data: PromoDomain, source: String) {
        val intent = Intent(requireContext(), PromoDetailActivity::class.java).apply {
            putExtra(PromoDetailActivity.EXTRA_PROMO, data)
            putExtra(PromoDetailActivity.EXTRA_SOURCE, source)
        }
        startActivity(intent)
    }

    private fun observePromos() {
        viewLifecycleOwner.lifecycleScope.launch {
            // LoadState observers
            launch {
                ajuanPromoAdapter.loadStateFlow.collect { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Loading -> {
                            showLoading()
                            binding.scrollView2.visibility = View.GONE
                            binding.tvTidakAdaRiwayatAjuan.visibility = View.GONE
                            Log.d("PromoFragment", "Ajuan Promo Loading")
                        }
                        is LoadState.NotLoading -> {
                            hideLoading()
                            binding.scrollView2.visibility = View.VISIBLE
                            Log.d("PromoFragment", "Ajuan Promo Ready")

                            // Check if adapter is empty
                            if (ajuanPromoAdapter.itemCount == 0) {
                                binding.tvTidakAdaRiwayatAjuan.visibility = View.VISIBLE
                                binding.rvPromoMitra.visibility = View.GONE
                            } else {
                                binding.tvTidakAdaRiwayatAjuan.visibility = View.GONE
                                binding.rvPromoMitra.visibility = View.VISIBLE
                            }

                            Log.d("PromoFragment", "Promo Ajuan Ready with ${ajuanPromoAdapter.itemCount} items")
                        }
                        is LoadState.Error -> {
                            hideLoading()
                            binding.scrollView2.visibility = View.VISIBLE
                            binding.tvTidakAdaRiwayatAjuan.visibility = View.GONE
                            showError((loadState.refresh as LoadState.Error).error.message)
                        }
                    }
                }
            }

            launch {
                promoMitraAdapter.loadStateFlow.collect { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Loading -> {
                            showLoading()
                            binding.scrollView2.visibility = View.GONE
                            binding.tvTidakAdaRiwayatMitra.visibility = View.GONE
                            Log.d("PromoFragment", "Promo Mitra Loading")
                        }
                        is LoadState.NotLoading -> {
                            hideLoading()
                            binding.scrollView2.visibility = View.VISIBLE
                            Log.d("PromoFragment", "Promo Mitra Ready")

                            if (promoMitraAdapter.itemCount == 0) {
                                binding.tvTidakAdaRiwayatMitra.visibility = View.VISIBLE
                                binding.rvPromoMitra.visibility = View.GONE
                            } else {
                                binding.tvTidakAdaRiwayatAjuan.visibility = View.GONE
                                binding.rvPromoMitra.visibility = View.VISIBLE
                            }

                            Log.d("PromoFragment", "Promo Mitra Ready with ${promoMitraAdapter.itemCount} items")
                        }
                        is LoadState.Error -> {
                            hideLoading()
                            binding.scrollView2.visibility = View.VISIBLE
                            binding.tvTidakAdaRiwayatMitra.visibility = View.GONE
                            showError((loadState.refresh as LoadState.Error).error.message)
                        }
                    }
                }
            }

            // Data collectors tetap sama
            launch {
                viewModel.getPromos(
                    category = "", // Kosong untuk ajuan promo
                    status = "draft", // Filter status draft langsung di API
                    name = "" // Kosong karena tidak ada pencarian
                ).collect { pagingData ->
                    ajuanPromoAdapter.submitData(pagingData)
                }
            }

            launch {
                viewModel.promos.collect { pagingData ->
                    promoMitraAdapter.submitData(pagingData)
                }
            }

        }
    }

    private fun validateToken() {
        promoViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        promoViewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.ADMIN
            setupUserVisibility(mockUserRole)

            Log.d("PromoFragment", "Current User Role: ${mockUserRole.name}")

//            Use This For Real
//            setupFabVisibility(userRole)

            ajuanPromoAdapter.setUserRole(mockUserRole)
            promoMitraAdapter.setUserRole(mockUserRole)

            Log.d("PromoFragment", "Role set to adapters: ${mockUserRole.name}")
        }
    }

    private fun setupUserVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.rvPromoCategory.visibility = View.GONE
                binding.tvAjuanPromo.visibility = View.VISIBLE
                binding.rvAjuanPromo.visibility = View.VISIBLE
                binding.tvPromoMitra.visibility = View.VISIBLE
                binding.rvPromoMitra.visibility = View.VISIBLE
                binding.tvTidakAdaRiwayatAjuan.visibility = View.GONE
                binding.tvTidakAdaRiwayatMitra.visibility = View.GONE
            }
            UserRole.MEMBER, UserRole.NONMEMBER -> {
                binding.rvPromoCategory.visibility = View.VISIBLE
                binding.tvAjuanPromo.visibility = View.GONE
                binding.rvAjuanPromo.visibility = View.GONE
                binding.tvPromoMitra.visibility = View.GONE
                binding.rvPromoMitra.visibility = View.VISIBLE
                binding.tvTidakAdaRiwayatAjuan.visibility = View.GONE
            }
            else -> {
                binding.rvPromoCategory.visibility = View.GONE
                binding.tvAjuanPromo.visibility = View.GONE
                binding.rvAjuanPromo.visibility = View.GONE
                binding.tvPromoMitra.visibility = View.GONE
                binding.rvPromoMitra.visibility = View.GONE
                binding.tvTidakAdaRiwayatAjuan.visibility = View.GONE
                binding.tvTidakAdaRiwayatMitra.visibility = View.GONE
            }
        }
        setTextVisibilityBasedOnLoading(binding.progressBar.visibility == View.VISIBLE, userRole)
    }

    private fun setTextVisibilityBasedOnLoading(isLoading: Boolean, userRole: UserRole) {
        binding.apply {
            if (isLoading) {
                tvPromoMitra.visibility = View.GONE
                tvAjuanPromo.visibility = View.GONE
            } else {
                when (userRole) {
                    UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                        tvPromoMitra.visibility = View.VISIBLE
                        tvAjuanPromo.visibility = View.VISIBLE
                    }
                    UserRole.MEMBER, UserRole.NONMEMBER -> {
                        tvPromoMitra.visibility = View.GONE
                        tvAjuanPromo.visibility = View.GONE
                    }
                    else -> {
                        tvPromoMitra.visibility = View.GONE
                        tvAjuanPromo.visibility = View.GONE
                    }
                }
            }
        }
    }

//    private fun setupRecyclerView() {
//        storyPagingAdapter = StoryPagingAdapter()
//
//
//        storyPagingAdapter.setOnItemClickCallback(object : StoryPagingAdapter.OnItemClickCallback {
//            override fun onItemClicked(context: Context, story: StoryDomainTester) {
//            }
//        })
//    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            // Sembunyikan text saat loading
            setTextVisibilityBasedOnLoading(true, getCurrentUserRole())
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
            // Tampilkan text sesuai role setelah loading selesai
            setTextVisibilityBasedOnLoading(false, getCurrentUserRole())
        }
    }

    // Helper function untuk mendapatkan current user role
    private fun getCurrentUserRole(): UserRole {
        // Gunakan mockUserRole untuk testing
        return UserRole.ADMIN // Sesuaikan dengan kebutuhan
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PROMO_SOURCE_AJUAN = "ajuan_promo"
        const val PROMO_SOURCE_MITRA = "mitra_promo"
    }
}