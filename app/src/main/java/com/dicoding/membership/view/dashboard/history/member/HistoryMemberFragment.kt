package com.dicoding.membership.view.dashboard.history.member

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.databinding.FragmentHistoryMemberBinding
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.HistoryDetailRiwayatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryMemberFragment : Fragment() {
    private var _binding: FragmentHistoryMemberBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryMemberViewModel by viewModels()
    private lateinit var adapter: HistoryMemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupObservers()
        binding.swipeRefresh.isRefreshing = false

        viewModel.getAllUsers()
    }

    private fun setupRecyclerView() {
        adapter = HistoryMemberAdapter().apply {
            setOnItemClickListener { userId ->
                val intent = Intent(requireContext(), HistoryDetailRiwayatActivity::class.java).apply {
                    putExtra(HistoryDetailRiwayatActivity.EXTRA_USER_ID, userId)
                }
                startActivity(intent)
            }
        }

        binding.recyclerViewHistoryMember.apply {
            adapter = this@HistoryMemberFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            // Reset data and reload
            viewModel.currentPage = 1
            viewModel.isLastPage = false
            viewModel.getAllUsers()
        }
    }

    private fun setupObservers() {
        viewModel.userList.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    // Always stop refreshing and hide loading when success
                    showLoading(false)

                    resource.data?.let { userList ->
                        if (viewModel.currentPage == 1) {
                            adapter.setData(userList.data)
                        } else {
                            adapter.addData(userList.data)
                        }

                    }
                }
                is Resource.Error -> {
                    // Always stop refreshing and hide loading when error
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    showLoading(false)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(boolean: Boolean){
        binding.loadingOverlay.visibility = if(boolean) View.VISIBLE else View.GONE
        binding.recyclerViewHistoryMember.visibility = if(!boolean) View.VISIBLE else View.GONE
    }
}