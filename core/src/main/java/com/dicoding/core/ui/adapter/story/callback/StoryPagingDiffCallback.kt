package com.dicoding.membership.core.ui.adapter.story.callback

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester

class StoryPagingDiffCallback : DiffUtil.ItemCallback<StoryDomainTester>() {
    override fun areItemsTheSame(oldItem: StoryDomainTester, newItem: StoryDomainTester): Boolean {
        val result = oldItem.id == newItem.id
        Log.d("StoryPagingDiffCallback", "areItemsTheSame: $result")
        return result
    }

    override fun areContentsTheSame(oldItem: StoryDomainTester, newItem: StoryDomainTester): Boolean {
        val result = oldItem == newItem
        Log.d("StoryPagingDiffCallback", "areContentsTheSame: $result")
        return result
    }
}