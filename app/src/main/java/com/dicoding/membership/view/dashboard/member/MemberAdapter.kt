package com.dicoding.membership.view.dashboard.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.databinding.ItemCardMemberBinding

class MemberAdapter : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    private var userList = ArrayList<User>()

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
                labelMembershipType.text = user.memberType ?: "Non-Member"
                tvUserName.text = user.name
                tvUserEmail.text = user.email
                tvUserPhone.text = user.phone ?: "-"
            }
        }
    }
}