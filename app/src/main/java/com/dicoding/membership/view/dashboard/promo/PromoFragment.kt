package com.dicoding.membership.view.dashboard.promo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.ui.adapter.story.StoryPagingAdapter
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.membership.databinding.FragmentPromoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PromoFragment : Fragment() {

    private var _binding: FragmentPromoBinding? = null
    private val binding get() = _binding!!

    private val promoViewModel: PromoViewModel by viewModels()
    private lateinit var storyPagingAdapter: StoryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPromoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        lifecycleScope.launch {
            promoViewModel.getStories("default_filter", false).collectLatest { pagingData ->
                storyPagingAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerView() {
        storyPagingAdapter = StoryPagingAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyPagingAdapter
        }

        storyPagingAdapter.setOnItemClickCallback(object : StoryPagingAdapter.OnItemClickCallback {
            override fun onItemClicked(context: Context, story: StoryDomainTester) {
                // Handle item click
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}