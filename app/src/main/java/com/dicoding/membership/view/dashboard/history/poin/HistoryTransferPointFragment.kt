package com.dicoding.membership.view.dashboard.history.poin

import DashboardPointHistoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentHistoryTransferPoinBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryTransferPointFragment : Fragment() {
    private var _binding: FragmentHistoryTransferPoinBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryTransferPointViewModel by viewModels()
    private lateinit var historyAdapter: DashboardPointHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryTransferPoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getUserData()

        viewModel.userData.observe(viewLifecycleOwner) { loginDomain ->
            loginDomain?.let { data ->
                setupRecyclerView(data.user.id)
                viewModel.getUserHistory(data.user.id)
            }
        }

        lifecycleScope.launch {
            viewModel.userHistory.collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Success -> {
                        showLoading(false)
                        resource.data?.let { history ->
                            historyAdapter.updateItems(history.history)
                        }
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        // Handle error
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerView(userId: String) {
        historyAdapter = DashboardPointHistoryAdapter(userId)
        binding.rvPoin.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvPoin.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
