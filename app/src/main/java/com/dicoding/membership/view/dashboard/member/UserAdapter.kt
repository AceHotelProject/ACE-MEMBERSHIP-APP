package com.dicoding.membership.view.dashboard.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemCardMemberBinding

class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private var userList = ArrayList<User>()
    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(newList: List<User>) {
        val oldSize = userList.size
        userList.addAll(newList)
        notifyItemRangeInserted(oldSize, newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    inner class ViewHolder(private val binding: ItemCardMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                // Set subscription type
                labelMembershipType.text = user.membership?.subscriptionType?.type ?: "-"

                // Set status with appropriate color
                val status = user.membership?.status ?: "-"
                labelPeriodType.text = status

                if (status.equals("active", ignoreCase = true)) {
                    labelPeriodType.setBackgroundResource(R.drawable.chip_category_green)
                    labelPeriodType.setTextColor(ContextCompat.getColor(root.context, R.color.green))
                } else {
                    labelPeriodType.setBackgroundResource(R.drawable.chip_category_red)
                    labelPeriodType.setTextColor(ContextCompat.getColor(root.context, R.color.red))
                }

                // Set user information
                tvUserName.text = user.name ?: "-"
                tvUserEmail.text = user.email ?: "-"
                tvUserPhone.text = user.phone ?: "-"

                // Set click listener
                root.setOnClickListener {
                    user.id?.let { userId ->
                        onItemClickListener?.invoke(userId)
                    }
                }
            }
        }
    }
}