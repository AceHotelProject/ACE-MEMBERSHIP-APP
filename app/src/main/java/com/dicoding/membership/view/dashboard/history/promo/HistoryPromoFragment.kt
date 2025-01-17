package com.dicoding.membership.view.dashboard.history.promo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.membership.databinding.FragmentHistoryPromoBinding
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Dicoding Test Pager
//        setupRecyclerView()
//        lifecycleScope.launch {
//            historyPromoViewModel.getStories("default_filter", false).collectLatest { pagingData ->
//                storyPagingAdapter.submitData(pagingData)
//            }
//        }

        setupRecyclerView()

        setupSwipeRefresh()

        validateToken()

        loadPromoHistory()
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
        historyAdapter = PromoHistoryAdapter()
        binding.rvPromo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
        Log.d("PromoHistoryFragment", "RecyclerView setup completed")
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
                    viewModel.getPromoHistory()
                        .collectLatest { pagingData ->
                            historyAdapter.submitData(pagingData)
                        }
                }

                // Handle loading state
                lifecycleScope.launch {
                    historyAdapter.loadStateFlow.collectLatest { loadState ->
                        // Update SwipeRefreshLayout state
                        binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading

                        when (loadState.refresh) {
                            is LoadState.Loading -> {
                                if (!binding.swipeRefresh.isRefreshing) {
                                    showLoading()
                                }
                                hideEmptyState()
                            }
                            is LoadState.Error -> {
                                hideLoading()
                                binding.swipeRefresh.isRefreshing = false
                                val error = (loadState.refresh as LoadState.Error).error
                                showError(error.message ?: "Terjadi kesalahan")
                                showEmptyState()
                            }
                            is LoadState.NotLoading -> {
                                hideLoading()
                                binding.swipeRefresh.isRefreshing = false
                                if (historyAdapter.itemCount == 0) {
                                    showEmptyState()
                                } else {
                                    hideEmptyState()
                                }
                            }
                        }
                    }
                }
            } else {
                binding.swipeRefresh.isRefreshing = false
                showError("Token kosong, silakan login ulang.")
            }
        }
    }

    private fun showEmptyState() {
        binding.apply {
            rvPromo.visibility = View.GONE
            tvTidakAdaRiwayat.visibility = View.VISIBLE
        }
    }

    private fun hideEmptyState() {
        Log.d("PromoHistoryFragment", "RecyclerView visibility set to VISIBLE")
        binding.apply {
            rvPromo.visibility = View.VISIBLE
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
