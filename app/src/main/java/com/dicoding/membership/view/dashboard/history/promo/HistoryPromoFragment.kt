package com.dicoding.membership.view.dashboard.history.promo

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
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.databinding.FragmentHistoryPromoBinding
import com.dicoding.membership.view.dashboard.history.historydetailpromo.HistoryDetailPromoActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryPromoFragment : Fragment() {

    private var _binding: FragmentHistoryPromoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryPromoViewModel by viewModels()
    private lateinit var historyAdapter: PromoHistoryAdapter


//    private val historyPromoViewModel: HistoryPromoViewModel by viewModels()
//    private lateinit var storyPagingAdapter: StoryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryPromoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Dicoding Test Pager
//        setupRecyclerView()
//        lifecycleScope.launch {
//            historyPromoViewModel.getStories("default_filter", false).collectLatest { pagingData ->
//                storyPagingAdapter.submitData(pagingData)
//            }
//        }

        checkUserRole()

//        setupRecyclerView()
//
//        setupSwipeRefresh()
//
//        validateToken()
//
//        loadPromoHistory()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        viewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.MEMBER
            setupUserVisibility(mockUserRole)

            // Only load promo history for authorized roles
            when (mockUserRole) {
                UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER, UserRole.USER -> {
                    setupRecyclerView()
                    setupSwipeRefresh()
                    validateToken()
                    loadPromoHistory()
                }
                else -> {
                    // Don't load data for unauthorized roles
                    Log.d("HistoryPromoFragment", "Unauthorized role: ${mockUserRole.name}, skipping data load")
                }
            }

            Log.d("HistoryPromoFragment", "Current User Role: ${mockUserRole.name}")
        }
    }

    private fun setupUserVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.USER, UserRole.MEMBER -> {
                binding.apply {
                    swipeRefresh.visibility = View.VISIBLE
                    layoutNonMember.visibility = View.GONE
                }
            }
           UserRole.NONMEMBER -> {
               binding.apply {
                   swipeRefresh.visibility = View.GONE
                   layoutNonMember.visibility = View.VISIBLE
                   rvPromoHistory.visibility = View.GONE
                   tvTidakAdaRiwayat.visibility = View.GONE
               }
            }
            else -> {
                binding.apply {
                    swipeRefresh.visibility = View.GONE
                    layoutNonMember.visibility = View.GONE
                    rvPromoHistory.visibility = View.GONE
                    tvTidakAdaRiwayat.visibility = View.GONE
                }
            }
        }
    }

//    private fun setupRecyclerView() {
//        storyPagingAdapter = StoryPagingAdapter()
//
//        binding.rvPromo.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = storyPagingAdapter
//        }
//
//        storyPagingAdapter.setOnItemClickCallback(object : StoryPagingAdapter.OnItemClickCallback {
//            override fun onItemClicked(context: Context, story: StoryDomainTester) {
////                val intent = Intent(context, DetailStoryActivity::class.java)
////                intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, story.id)
////                startActivity(intent)
//                val uri = Uri.parse("favoriteapp://story/${story.id}")
//                val intent = Intent(Intent.ACTION_VIEW, uri)
//                startActivity(intent)
//            }
//        })
//    }

    private fun setupRecyclerView() {
        historyAdapter = PromoHistoryAdapter().apply {
            // Set the onItemClickCallback
            onItemClickCallback = object : PromoHistoryAdapter.OnItemClickCallback {
                override fun onItemClicked(history: PromoHistoryDomain) {
                    // Handle item click here
                    navigateToDetailActivity(history)
                }
            }
        }

        binding.rvPromoHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        Log.d("PromoHistoryFragment", "RecyclerView setup completed")
    }

    private fun navigateToDetailActivity(history: PromoHistoryDomain) {
        val intent = Intent(requireContext(), HistoryDetailPromoActivity::class.java).apply {
            putExtra("PROMO_HISTORY", history)
        }
        startActivity(intent)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            historyAdapter.refresh() // Ini akan memicu reload data dari awal
        }
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            Log.d("PromoHistoryFragment", "Token: $token")
            if (token.isEmpty()) {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun loadPromoHistory() {
        viewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                lifecycleScope.launch {
                    launch {
                        viewModel.getPromoHistory(
                            promoCategory = viewModel.selectedCategory.value
                        ).collect { pagingData ->
                            historyAdapter.submitData(pagingData)
                        }
                    }

                    historyAdapter.addLoadStateListener { loadState ->
                        Log.d("PromoHistory", "LoadState: ${loadState.refresh}")
                        Log.d("PromoHistory", "ItemCount: ${historyAdapter.itemCount}")

                        // Immediately set swipeRefresh to false to hide its progress indicator
                        binding.swipeRefresh.isRefreshing = false

                        when (loadState.refresh) {
                            is LoadState.Loading -> {
                                Log.d("PromoHistory", "State: Loading")
                                showLoading()
                                hideEmptyState()
                            }
                            is LoadState.Error -> {
                                Log.d("PromoHistory", "State: Error")
                                hideLoading()
                                showError((loadState.refresh as LoadState.Error).error.message)
                            }
                            is LoadState.NotLoading -> {
                                Log.d("PromoHistory", "State: NotLoading")
                                hideLoading()
                                if (historyAdapter.itemCount == 0) {
                                    Log.d("PromoHistory", "Showing empty state")
                                    showEmptyState()
                                } else {
                                    Log.d("PromoHistory", "Hiding empty state")
                                    hideEmptyState()
                                }
                            }
                        }
                    }
                }
            } else {
                showError("Token kosong, silakan login ulang.")
            }
        }
    }

    private fun showEmptyState() {
        binding.apply {
            rvPromoHistory.visibility = View.GONE
            tvTidakAdaRiwayat.visibility = View.VISIBLE
        }
    }

    private fun hideEmptyState() {
        Log.d("PromoHistoryFragment", "RecyclerView visibility set to VISIBLE")
        binding.apply {
            rvPromoHistory.visibility = View.VISIBLE
            tvTidakAdaRiwayat.visibility = View.GONE
        }
    }


    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
        }
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
