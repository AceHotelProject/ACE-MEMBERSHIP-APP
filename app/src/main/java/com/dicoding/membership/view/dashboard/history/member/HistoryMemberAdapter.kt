package com.dicoding.membership.view.dashboard.history.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.membership.model.Subscription
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemCardMemberBinding

class HistoryMemberAdapter : RecyclerView.Adapter<HistoryMemberAdapter.ViewHolder>() {
    private val subscriptions = mutableListOf<Subscription>()
    private var onItemClickListener: ((String) -> Unit)? = null
    private var onLoadMoreListener: (() -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnLoadMoreListener(listener: () -> Unit) {
        onLoadMoreListener = listener
    }

    fun submitList(newSubscriptions: List<Subscription>, isRefresh: Boolean) {
        if (isRefresh) {
            subscriptions.clear()
        }
        val startPosition = subscriptions.size
        subscriptions.addAll(newSubscriptions)
        if (isRefresh) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeInserted(startPosition, newSubscriptions.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = subscriptions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subscriptions[position])

        // Trigger load more when we're at the end of the list
        if (position == subscriptions.size - 1) {
            onLoadMoreListener?.invoke()
        }
    }

    inner class ViewHolder(private val binding: ItemCardMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subscription: Subscription) {
            with(binding) {
                // Set membership type
                labelMembershipType.text = subscription.subscriptionType

                // Set status with appropriate color
                labelPeriodType.text = subscription.status
                if (subscription.status.equals("active", ignoreCase = true)) {
                    labelPeriodType.setBackgroundResource(R.drawable.chip_category_green)
                    labelPeriodType.setTextColor(ContextCompat.getColor(root.context, R.color.green))
                } else {
                    labelPeriodType.setBackgroundResource(R.drawable.chip_category_orange)
                    labelPeriodType.setTextColor(ContextCompat.getColor(root.context, R.color.orange_100))
                }

                // Set user information
                tvUserName.text = subscription.userId?.name ?: "-"
                tvUserEmail.text = subscription.userId?.id ?: "-"
                tvUserPhone.text = "-" // Phone not available in subscription data

                // Set click listener
                root.setOnClickListener {
                    subscription.userId?.id?.let { userId ->
                        onItemClickListener?.invoke(userId)
                    }
                }
            }
        }
    }
}