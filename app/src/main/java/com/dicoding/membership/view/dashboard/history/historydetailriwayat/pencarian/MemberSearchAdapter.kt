package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.databinding.ItemCardMemberBinding

class MemberSearchAdapter : RecyclerView.Adapter<MemberSearchAdapter.ViewHolder>() {
    private var completeUserList = ArrayList<User>()
    private var displayedUserList = ArrayList<User>()
    private var onItemClickListener: ((User) -> Unit)? = null

    fun addData(newList: List<User>) {
        val oldSize = completeUserList.size
        completeUserList.addAll(newList)
        displayedUserList.addAll(newList)  // Add to both lists initially
        notifyItemRangeInserted(oldSize, newList.size)
    }

    fun clearData() {
        completeUserList.clear()
        displayedUserList.clear()
        notifyDataSetChanged()
    }

    fun applyFilter(predicate: (User) -> Boolean) {
        displayedUserList.clear()
        displayedUserList.addAll(completeUserList.filter(predicate))
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = displayedUserList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(displayedUserList[position])
    }

    inner class ViewHolder(private val binding: ItemCardMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                labelMembershipType.text = user.memberType ?: "Non-Member"
                tvUserName.text = user.name
                tvUserEmail.text = user.email
                tvUserPhone.text = user.phone ?: "-"

                root.setOnClickListener {
                    onItemClickListener?.invoke(user)
                }
            }
        }
    }
}