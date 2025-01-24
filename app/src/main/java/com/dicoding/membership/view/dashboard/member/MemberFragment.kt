package com.dicoding.membership.view.dashboard.member

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentMemberBinding
import com.dicoding.membership.databinding.FragmentMitraBinding
import com.dicoding.membership.view.dashboard.member.listeditmember.ListEditMemberActivity
import com.dicoding.membership.view.dashboard.mitra.MitraViewModel
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemberFragment : Fragment() {

    private var _binding: FragmentMemberBinding? = null
    private val binding get() = _binding!!
    private lateinit var memberAdapter: MemberAdapter

    private val memberViewModel: MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView implementation
        setupRecyclerView()
        observeData()

        validateToken()
        handleMenuButton()
    }

    private fun observeData() {
        memberViewModel.getAllUsers()
        memberViewModel.userList.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { userList ->
                        if (memberViewModel.currentPage == 1) {
                            memberAdapter.setData(userList.data)
                        } else {
                            memberAdapter.addData(userList.data)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                }

                else -> {}
            }
        }
    }

    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter()
        binding.listMemberRecyclerview.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            layoutManager = LinearLayoutManager(context)
            adapter = memberAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        memberViewModel.loadMoreUsers()
                    }
                }
            })
        }
    }

    private fun validateToken() {
        memberViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun handleMenuButton() {
        binding.layoutEditMembership.setOnClickListener {
            val intent = Intent(requireActivity(), ListEditMemberActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(boolean: Boolean){
        binding.loadingOverlay.visibility = if(boolean) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}