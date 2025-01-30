package com.dicoding.membership.view.dashboard.history.member

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.databinding.FragmentHistoryMemberBinding
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.HistoryDetailRiwayatActivity
import com.dicoding.membership.view.dashboard.member.detailmember.DetailMemberActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryMemberFragment : Fragment() {
    private var _binding: FragmentHistoryMemberBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryMemberViewModel by viewModels()
    private lateinit var adapter: HistoryMemberAdapter

    private var isLoading = false
    private var hasMorePages = true

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
        setupObservers()

        viewModel.getAllUsers(isRefresh = true)
    }

    private fun setupRecyclerView() {
        adapter = HistoryMemberAdapter().apply {
            setOnLoadMoreListener {
                if (!isLoading && hasMorePages) {
                    viewModel.loadNextPage()
                }
            }
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

    private fun setupObservers() {
        viewModel.userList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    isLoading = true
                    if (viewModel.currentPage == 1) {
                        showLoading(true)
                        binding.tvTidakAdaRiwayat.visibility = View.GONE
                    }
                }
                is Resource.Success -> {
                    isLoading = false
                    showLoading(false)
                    resource.data?.let { userList ->
                        hasMorePages = viewModel.currentPage < userList.totalPages

                        binding.tvTidakAdaRiwayat.visibility =
                            if (userList.data.isEmpty() && viewModel.currentPage == 1) View.VISIBLE else View.GONE

                        adapter.submitList(
                            userList.data,
                            isRefresh = viewModel.currentPage == 1
                        )
                    }
                }
                is Resource.Error -> {
                    isLoading = false
                    showLoading(false)
                    if (viewModel.currentPage == 1) {
                        binding.tvTidakAdaRiwayat.visibility = View.VISIBLE
                        binding.recyclerViewHistoryMember.visibility = View.GONE
                    }
                    // You might want to show an error message here
                }

                else -> {}
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