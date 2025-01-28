package com.dicoding.membership.view.dashboard.mitra

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.databinding.FragmentMitraBinding
import com.dicoding.membership.view.dashboard.admin.manajemenmitra.MerchantPagingAdapter
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MitraFragment : Fragment() {

    private var _binding: FragmentMitraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MitraViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMitraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateToken()

        handleMenuButton()

        checkSavedMerchant()
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun handleMenuButton() {
        binding.tvStatistik.setOnClickListener {
//            val intent = Intent(requireActivity(), StatistikActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun checkSavedMerchant() {
        viewModel.getMerchantId().observe(viewLifecycleOwner) { savedId ->
            if (savedId.isEmpty()) {
                getFirstMerchant()
            } else {
                // Load merchant with saved ID
                loadMerchantData(savedId)
            }
        }
    }

    private fun getFirstMerchant() {
        lifecycleScope.launch {
            Log.d("MitraFragment", "Starting getFirstMerchant")
            val tempAdapter = MerchantPagingAdapter()

            // Launch untuk loadState
            lifecycleScope.launch {
                tempAdapter.loadStateFlow.collect { loadState ->
                    Log.d("MitraFragment", "Load state: ${loadState.refresh}")
                    if (loadState.refresh is LoadState.NotLoading) {
                        val firstMerchant = tempAdapter.snapshot().firstOrNull()
                        if (firstMerchant != null) {
                            Log.d("MitraFragment", "Found first merchant: ${firstMerchant.id}")
                            viewModel.saveMerchantId(firstMerchant.id).observe(viewLifecycleOwner) { saved ->
                                if (saved) loadMerchantData(firstMerchant.id)
                            }
                        }
                    }
                }
            }

            // Launch untuk paging data
            viewModel.getMerchants().collect { pagingData ->
                Log.d("MitraFragment", "Submitting paging data")
                tempAdapter.submitData(pagingData)
            }
        }
    }

    private fun loadMerchantData(merchantId: String) {
        Log.d("MitraFragment", "Loading merchant data for ID: $merchantId")
        viewModel.getMerchantsById(merchantId).observe(viewLifecycleOwner) { result ->
            when(result) {
                is Resource.Success -> {
                    hideLoading()
                    result.data?.let { merchant ->
                        binding.apply {
                            Log.d("MitraFragment", "Successfully loaded merchant: ${result.data?.name}")
//                            ivMitra
//                            tvMitraOwner.text = merchant
                            tvMitraType.text = merchant.merchantType
                            tvMitraName.text = merchant.name
//                            tvMitraPromo.text = merchant
//                            tvMitraPromoUse.text = merchant
//                            tvMitraPoin.text = merchant
//                            tvMitraPoinTerima.text = merchant
//                            tvMitraPoinTransfer.text = merchant
//                            tvMitraDescription.text = merchant.detail
                            // Update other UI elements
                        }
                    }
                }
                is Resource.Loading -> showLoading()
                is Resource.Error -> {
                    hideLoading()
                    Log.e("MitraFragment", "Failed to load merchant: ${result.message}")
                }
                else -> { }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            loadingOverlay.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
            loadingOverlay.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}