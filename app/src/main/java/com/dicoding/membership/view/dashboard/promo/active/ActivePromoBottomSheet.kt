package com.dicoding.membership.view.dashboard.promo.active

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityActivePromoBottomSheetBinding
import com.dicoding.membership.view.dashboard.promo.PromoAdapter
import com.dicoding.membership.view.dashboard.promo.detail.detailpromo.PromoDetailActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivePromoBottomSheet : BottomSheetDialogFragment() {
    private var _binding: ActivityActivePromoBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var promoAdapter: PromoAdapter

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    private val viewModel: ActivePromoBottomSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityActivePromoBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observePromos()
    }

    private fun setupRecyclerView() {
        promoAdapter = PromoAdapter().apply {
            setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
                override fun onItemClicked(data: PromoDomain) {
                    navigateToDetail(data, PROMO_SOURCE_MITRA)
                }
            })
        }

        binding.rvPromoMitra.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = promoAdapter
        }
    }

    private fun observePromos() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe load states
            launch {
                promoAdapter.loadStateFlow.collect { loadState ->
                    Log.d(TAG, "Load state changed: ${loadState.refresh}")
                    when (loadState.refresh) {
                        is LoadState.Loading -> {
                            Log.d(TAG, "Entering Loading state")
                            showLoading()
                            binding.rvPromoMitra.visibility = View.GONE
                        }
                        is LoadState.NotLoading -> {
                            Log.d(TAG, "Entering NotLoading state")
                            hideLoading()
                            binding.rvPromoMitra.visibility = View.VISIBLE

                            // Tambahkan delay kecil untuk memastikan adapter telah diperbarui
                            kotlinx.coroutines.delay(100)

                            val itemCount = promoAdapter.itemCount
                            Log.d(TAG, "Promo item count: $itemCount")

                            if (itemCount == 0) {
                                Log.d(TAG, "No items found")
                                // Anda bisa menambahkan TextView untuk menampilkan pesan "Tidak ada data"
                                binding.rvPromoMitra.visibility = View.GONE
                            } else {
                                binding.rvPromoMitra.visibility = View.VISIBLE
                            }
                        }
                        is LoadState.Error -> {
                            val error = (loadState.refresh as LoadState.Error)
                            Log.e(TAG, "Error loading promos: ${error.error.message}")
                            hideLoading()
                            binding.rvPromoMitra.visibility = View.GONE
                            showError(error.error.message)
                        }
                    }
                }
            }

            // Collect paging data
            launch {
                viewModel.promos.collect { pagingData ->
                    Log.d(TAG, "Submitting new paging data to adapter")
                    promoAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(message: String?) {
        Toast.makeText(
            requireContext(),
            message ?: "Terjadi kesalahan",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateToDetail(data: PromoDomain, source: String) {
        val intent = Intent(requireContext(), PromoDetailActivity::class.java).apply {
            putExtra(PromoDetailActivity.EXTRA_PROMO, data)
            putExtra(PromoDetailActivity.EXTRA_SOURCE, source)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ActivePromoBottomSheet"
        const val PROMO_SOURCE_MITRA = "mitra_promo"
    }
}