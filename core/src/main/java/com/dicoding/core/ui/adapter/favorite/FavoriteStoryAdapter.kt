package com.dicoding.core.ui.adapter.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.core.R
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester

class FavoriteStoryAdapter : RecyclerView.Adapter<FavoriteStoryAdapter.ViewHolder>() {

    private val storyList = mutableListOf<StoryDomainTester>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setStories(stories: List<StoryDomainTester>) {
        storyList.clear()
        storyList.addAll(stories)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItemPhotoUrl: ImageView = itemView.findViewById(R.id.iv_item_photo_url)
        private val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        private val tvItemCreatedAt: TextView = itemView.findViewById(R.id.tv_item_createdAt)
        private val tvItemDescription: TextView = itemView.findViewById(R.id.tv_item_description)

        fun bind(story: StoryDomainTester) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(ivItemPhotoUrl)

            tvItemName.text = story.name
            tvItemCreatedAt.text = story.createdAt
            tvItemDescription.text = story.description

            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(itemView.context, story)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount(): Int = storyList.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(context: Context, story: StoryDomainTester)
    }
}