package com.dicoding.core.ui.adapter.story

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.core.R
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.membership.core.ui.adapter.story.callback.StoryPagingDiffCallback

class StoryPagingAdapter :
    PagingDataAdapter<StoryDomainTester, StoryPagingAdapter.ViewHolder>(StoryPagingDiffCallback()) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItemPhotoUrl: ImageView = itemView.findViewById(R.id.iv_item_photo_url)
        private val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        private val tvItemCreatedAt: TextView = itemView.findViewById(R.id.tv_item_createdAt)
        private val tvItemDescription: TextView = itemView.findViewById(R.id.tv_item_description)

        fun bind(story: StoryDomainTester) {
            Log.d("StoryPagingAdapter", "Binding story: $story")

            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(ivItemPhotoUrl)
                .also {
                    Log.d("StoryPagingAdapter", "Loading image for story: ${story.photoUrl}")
                }

            tvItemName.text = story.name
            tvItemCreatedAt.text = story.createdAt
            tvItemDescription.text = story.description

            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(itemView.context, story)
                Log.d("StoryPagingAdapter", "Story clicked: $story")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        Log.d("StoryPagingAdapter", "Creating ViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            Log.d("StoryPagingAdapter", "Binding ViewHolder at position: $position with story: $story")
            holder.bind(story)
        } else {
            Log.d("StoryPagingAdapter", "Binding ViewHolder at position: $position with null story")
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
        Log.d("StoryPagingAdapter", "OnItemClickCallback set")
    }

    interface OnItemClickCallback {
        fun onItemClicked(context: Context, story: StoryDomainTester)
    }
}

