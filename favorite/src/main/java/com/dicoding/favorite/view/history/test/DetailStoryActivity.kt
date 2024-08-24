package com.dicoding.favorite.view.history.test

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.favorite.view.utils.DaggerStoryComponent
import com.dicoding.favorite.view.utils.ViewModelFactory
import com.dicoding.membership.di.FavoriteModuleDependencies
import com.dicoding.story.favorite.R
import com.dicoding.story.favorite.databinding.ActivityDetailStoryBinding
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class DetailStoryActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelFactory
    private lateinit var binding: ActivityDetailStoryBinding

//    private val detailStoryViewModel: DetailStoryViewModel by viewModels{
//        factory
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerStoryComponent.builder()
            .context(this)
            .appDependencies(
                EntryPointAccessors.fromApplication(
                    applicationContext,
                    FavoriteModuleDependencies::class.java
                )
            )
            .build()
            .inject(this)

        Log.d("DetailStoryActivity", "Dependencies injected successfully")
        Log.d("DetailStoryActivity", "Factory injected: $factory")

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStoryViewModel = ViewModelProvider(this, factory)[DetailStoryViewModel::class.java]
        Log.d("DetailStoryActivity", "Manual ViewModel creation: $detailStoryViewModel")

        val storyId = intent.data?.lastPathSegment
        Log.d("DetailStoryActivity", "storyId from Uri: $storyId")
        if (storyId != null) {
            detailStoryViewModel.loadStoryDetails(storyId)
            detailStoryViewModel.checkIfFavorite(storyId)
        } else {
            Log.e("DetailStoryActivity", "storyId is null or not provided in Uri")
        }


        detailStoryViewModel.story.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("DetailStoryActivity", "Loading story details...")
                    binding.progressBar3.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    Log.d("DetailStoryActivity", "Story details loaded successfully: ${resource.data}")
                    binding.progressBar3.visibility = View.GONE
                    val story = resource.data
                    binding.apply {
                        tvDetailTitle.text = story?.name
                        tvcreatedAt.text = story?.createdAt
                        tvDetailDescription.text = story?.description
                        tvId.text = story?.id
                        tvlon.text = story?.lon.toString()
                        tvlat.text = story?.lat.toString()
                        Glide.with(this@DetailStoryActivity)
                            .load(story?.photoUrl)
                            .into(ivDetailPhoto)
                    }
                }
                is Resource.Error -> {
                    Log.e("DetailStoryActivity", "Error loading story details: ${resource.message}")
                    binding.progressBar3.visibility = View.GONE
                }
                else -> {}
            }
        }

        binding.btnFavorite.setOnClickListener {
            val story = detailStoryViewModel.story.value?.data
            if (story != null) {
                if (detailStoryViewModel.isFavorite.value == true) {
                    Log.d("DetailStoryActivity", "Removing story from favorites: $story")
                    detailStoryViewModel.removeFavorite(story)
                } else {
                    Log.d("DetailStoryActivity", "Adding story to favorites: $story")
                    detailStoryViewModel.addFavorite(story)
                }
            }
        }

        detailStoryViewModel.isFavorite.observe(this) { isFavorite ->
            if (isFavorite) {
                Log.d("DetailStoryActivity", "Story is marked as favorite")
                binding.btnFavorite.setImageResource(R.drawable.icons_favorite_clicked)
            } else {
                Log.d("DetailStoryActivity", "Story is not marked as favorite")
                binding.btnFavorite.setImageResource(R.drawable.icons_favorite_no_clicked)
            }
        }
    }

//    companion object {
//        const val EXTRA_STORY_ID = "extra_story_id"
//    }
}