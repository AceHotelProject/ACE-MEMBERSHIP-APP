package com.dicoding.membership.view.dashboard.admin.manajemenmitra

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.merchants.model.MerchantResultDomain
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemManajemenMitraBinding

class MerchantPagingAdapter : PagingDataAdapter<MerchantResultDomain, MerchantPagingAdapter.MerchantViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null
    private var onMenuClickCallback: OnMenuClickCallback? = null

    private var selectedPosition = -1

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setOnMenuClickCallback(onMenuClickCallback: OnMenuClickCallback) {
        this.onMenuClickCallback = onMenuClickCallback
    }

    fun clearSelection() {
        val oldPosition = selectedPosition
        selectedPosition = -1
        notifyItemChanged(oldPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        val binding = ItemManajemenMitraBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MerchantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        val data = getItem(position)
        Log.d("MerchantAdapter", "Binding merchant at position $position - Name: ${data?.name}")
        data?.let { holder.bind(it) }
    }

    inner class MerchantViewHolder(private val binding: ItemManajemenMitraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MerchantResultDomain) {
            with(binding) {
                // Set merchant name
                adminManajemenMitraName.text = data.name

                // Set merchant type/category
                tvPromoLabel.text = data.merchantType

                // Set owner description (email - phone)
                val ownerInfo = data.userId.firstOrNull()
                manajemenMitraOwnerDescription.text = if (ownerInfo != null) {
                    "${ownerInfo.email} - ${ownerInfo.phone}"
                } else {
                    "-"
                }

                // Set staff description (email - phone)
                val staffInfo = if (data.userId.size > 1) {
                    data.userId.drop(1).joinToString("\n") { user ->
                        "${user.email} - ${user.phone}"
                    }
                } else {
                    "-"
                }
                manajemenMitraStaffDescription.text = staffInfo

                // Set background based on selection
                root.setBackgroundResource(
                    if (adapterPosition == selectedPosition) {
                        R.drawable.custom_border_orange
                    } else {
                        R.drawable.custom_border_grey
                    }
                )

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition

                    // Update both old and new positions
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    onItemClickCallback?.onItemClicked(data)
                }

                adminManajemenMitraDotMenu.setOnClickListener {
                    onMenuClickCallback?.onMenuClicked(it, data)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MerchantResultDomain)
    }

    interface OnMenuClickCallback {
        fun onMenuClicked(view: View, data: MerchantResultDomain)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MerchantResultDomain>() {
            override fun areItemsTheSame(
                oldItem: MerchantResultDomain,
                newItem: MerchantResultDomain
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MerchantResultDomain,
                newItem: MerchantResultDomain
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}