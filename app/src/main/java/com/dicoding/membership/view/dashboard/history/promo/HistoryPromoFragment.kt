package com.dicoding.membership.view.dashboard.history.promo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.membership.databinding.FragmentHistoryPromoBinding
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

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
            loadPromoHistory()
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
                showLoading()
                viewModel.getPromoHistory().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Resource.Success -> {
                            hideLoading()
                            Log.d("PromoHistoryFragment", "Data loaded successfully")
                            handleSuccess(result.data ?: emptyList())
                        }
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Error -> {
                            hideLoading()
                            showError(result.message)
                            showEmptyState()
                        }
                        else -> {
                            hideLoading()
                            showError("Terjadi kesalahan")
                            showEmptyState()
                        }
                    }
                }
            } else {
                showError("Token kosong, silakan login ulang.")
            }
        }
    }


    private fun handleSuccess(data: List<PromoHistoryDomain>) {
        Log.d("PromoHistoryFragment", "Data received: ${data.size} items")
        binding.apply {
            if (data.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                historyAdapter.submitList(data)
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
