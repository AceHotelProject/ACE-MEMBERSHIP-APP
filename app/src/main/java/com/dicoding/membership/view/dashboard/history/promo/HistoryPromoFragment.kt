package com.dicoding.membership.view.dashboard.history.promo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.ui.adapter.story.StoryPagingAdapter
import com.dicoding.dynamicfeature.view.dashboard.history.promo.HistoryPromoViewModel
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.membership.databinding.FragmentHistoryPromoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryPromoFragment : Fragment() {

    private var _binding: FragmentHistoryPromoBinding? = null
    private val binding get() = _binding!!

    private val historyPromoViewModel: HistoryPromoViewModel by viewModels()
    private lateinit var storyPagingAdapter: StoryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryPromoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        lifecycleScope.launch {
            historyPromoViewModel.getStories("default_filter", false).collectLatest { pagingData ->
                storyPagingAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerView() {
        storyPagingAdapter = StoryPagingAdapter()

        binding.rvPromo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyPagingAdapter
        }

        storyPagingAdapter.setOnItemClickCallback(object : StoryPagingAdapter.OnItemClickCallback {
            override fun onItemClicked(context: Context, story: StoryDomainTester) {
//                val intent = Intent(context, DetailStoryActivity::class.java)
//                intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, story.id)
//                startActivity(intent)
                val uri = Uri.parse("favoriteapp://story/${story.id}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
