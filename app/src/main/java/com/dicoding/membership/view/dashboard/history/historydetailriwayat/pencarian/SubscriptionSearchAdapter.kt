package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.membership.model.Subscription
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemCardMemberBinding

class SubscriptionSearchAdapter : RecyclerView.Adapter<SubscriptionSearchAdapter.ViewHolder>() {
    private var subscriptionList = ArrayList<Subscription>()
    private var onItemClickListener: ((String) -> Unit)? = null

    fun setData(newList: List<Subscription>) {
        subscriptionList.clear()
        subscriptionList.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearData() {
        subscriptionList.clear()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = subscriptionList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subscriptionList[position])
    }

    inner class ViewHolder(private val binding: ItemCardMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subscription: Subscription) {
            with(binding) {
                // Set subscription type
                labelMembershipType.text = subscription.subscriptionType

                // Set status with appropriate color
                labelPeriodType.text = subscription.status
                if (subscription.status.equals("active", ignoreCase = true)) {
                    labelPeriodType.setBackgroundResource(R.drawable.chip_category_green)
                    labelPeriodType.setTextColor(ContextCompat.getColor(root.context, R.color.green))
                } else {
                    labelPeriodType.setBackgroundResource(R.drawable.chip_category_red)
                    labelPeriodType.setTextColor(ContextCompat.getColor(root.context, R.color.red))
                }

                // Set user information
                tvUserName.text = subscription.userId?.name ?: "-"
                tvUserEmail.text = subscription.userId?.id ?: "-"
                tvUserPhone.text = "-"  // Not available in subscription data

                // Set click listener to pass the user ID
                root.setOnClickListener {
                    subscription.userId?.id?.let { userId ->
                        onItemClickListener?.invoke(userId)
                    }
                }
            }
        }
    }
}