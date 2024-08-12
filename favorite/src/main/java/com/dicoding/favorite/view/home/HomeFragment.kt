package com.dicoding.favorite.view.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.ui.adapter.favorite.FavoriteStoryAdapter
import com.dicoding.favorite.view.utils.DaggerHomeComponent
import com.dicoding.favorite.view.utils.ViewModelFactory
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.membership.di.ListFavoriteModuleDependencies
import com.dicoding.story.favorite.databinding.FragmentHomeBinding
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: ViewModelFactory
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var favoriteStoryAdapter: FavoriteStoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerHomeComponent.builder()
            .context(requireContext())
            .appDependencies(
                EntryPointAccessors.fromApplication(
                    requireContext().applicationContext,
                    ListFavoriteModuleDependencies::class.java
                )
            )
            .build()
            .inject(this)


        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setupRecyclerView()

        homeViewModel.getFavoriteStories().observe(viewLifecycleOwner) { stories ->
            favoriteStoryAdapter.setStories(stories)
        }
    }

    private fun setupRecyclerView() {
        favoriteStoryAdapter = FavoriteStoryAdapter()

        binding.rvPromo.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = favoriteStoryAdapter
        }

        favoriteStoryAdapter.setOnItemClickCallback(object : FavoriteStoryAdapter.OnItemClickCallback {
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