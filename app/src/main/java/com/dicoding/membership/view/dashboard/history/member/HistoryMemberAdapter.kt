package com.dicoding.membership.view.dashboard.history.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.databinding.ItemCardMemberBinding

class HistoryMemberAdapter : RecyclerView.Adapter<HistoryMemberAdapter.ViewHolder>() {
    private val items = mutableListOf<User>()
    private var onLoadMoreListener: (() -> Unit)? = null
    private var onItemClickListener: ((String) -> Unit)? = null

    inner class ViewHolder(
        private val binding: ItemCardMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                labelMembershipType.text = user.memberType ?: "Non-Member"
                tvUserName.text = user.name
                tvUserEmail.text = user.email
                tvUserPhone.text = user.phone ?: "Empty"

                root.setOnClickListener {
                    onItemClickListener?.invoke(user.id)  // Pass only the ID
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // This was missing - causing the NotImplementedError
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])

        if (position == items.size - 3) {
            onLoadMoreListener?.invoke()
        }
    }

    fun submitList(newItems: List<User>, isRefresh: Boolean = false) {
        if (isRefresh) {
            items.clear()
        }
        val oldSize = items.size
        items.addAll(newItems)
        if (isRefresh) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeInserted(oldSize, newItems.size)
        }
    }

    fun setOnLoadMoreListener(listener: () -> Unit) {
        onLoadMoreListener = listener
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }
}