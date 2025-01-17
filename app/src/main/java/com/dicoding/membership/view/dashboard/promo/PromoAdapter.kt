package com.dicoding.membership.view.dashboard.promo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemDashboardPromoBinding

class PromoAdapter : PagingDataAdapter<PromoDomain, PromoAdapter.PromoViewHolder>(DIFF_CALLBACK) {
    private var onItemClickCallback: OnItemClickCallback? = null

    private val listPromos = mutableListOf<PromoDomain>()

    private var isPaging = true

    // Default
    private var userRole: UserRole = UserRole.NONMEMBER

    fun setUserRole(role: UserRole) {
        Log.d("PromoAdapter", "Setting user role to: ${role.name}")
        userRole = role
        notifyDataSetChanged()
        Log.d("PromoAdapter", "Data set changed after role update")
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun submitList(promos: List<PromoDomain>) {
        Log.d("PromoAdapter", "Submitting new list with size: ${promos.size}")
        promos.forEach { promo ->
            Log.d("PromoAdapter", "List item - Name: ${promo.name}, isActive: ${promo.isActive}")
        }
        isPaging = false
        listPromos.clear()
        listPromos.addAll(promos)
        notifyDataSetChanged()
        Log.d("PromoAdapter", "List updated, new size: ${listPromos.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val binding = ItemDashboardPromoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PromoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        Log.d("PromoAdapter", "onBindViewHolder called. Position: $position, isPaging: $isPaging")
        if (isPaging) {
            val data = getItem(position)
            Log.d("PromoAdapter", "Paging item at position $position - Name: ${data?.name}, isActive: ${data?.isActive}")
            data?.let { holder.bind(it) }
        } else {
            if (position < listPromos.size) {
                val item = listPromos[position]
                Log.d("PromoAdapter", "Non-paging item at position $position - Name: ${item.name}, isActive: ${item.isActive}")
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
        val count = if (isPaging) {
            super.getItemCount()
        } else {
            listPromos.size
        }
        Log.d("PromoAdapter", "getItemCount called. Count: $count")
        return count
    }

    inner class PromoViewHolder(private val binding: ItemDashboardPromoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PromoDomain) {
            with(binding) {
                detailPromoTitle.text = data.name
                detailPromoDescription.text = data.detail
                detailPromoCategory.text = data.category

                Log.d("PromoAdapter", "Binding item with role: ${userRole.name}")

                // Load gambar pertama jika ada
                if (data.pictures.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(data.pictures[0])
                        .centerCrop()
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(itemDashboardPromoImageView)
                }

                when (userRole) {
                    UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                        // Custom behavior for ADMIN
                        layoutUser.visibility = View.GONE
                        Log.d("PromoAdapter", "Setting ADMIN/MITRA/RECEPTIONIST visibility")
                    }
                    UserRole.MEMBER, UserRole.NONMEMBER -> {
                        // Custom behavior for MEMBER
                        layoutUser.visibility = View.VISIBLE
                        Log.d("PromoAdapter", "Setting MEMBER and NONMEMBER visibility")
                    }
                    else -> {
                        // Default behavior
                        layoutUser.visibility = View.GONE
                    }
                }

                if (layoutUser.visibility == View.VISIBLE) {
                    itemDashboardPromoCode.text = data.token
                    itemDashboardPromoExpiryTime.text = data.endDate
                    Log.d("PromoAdapter", "Setting code: ${data.token}")
                    Log.d("PromoAdapter", "Setting expiry: ${data.endDate}")
                }
                if (layoutUser.visibility == View.VISIBLE) {
                    itemDashboardPromoCode.text = data.token
                    itemDashboardPromoExpiryTime.text = data.endDate
                    Log.d("PromoAdapter", "Setting code: ${data.token}")
                    Log.d("PromoAdapter", "Setting expiry: ${data.endDate}")
                }

                root.setOnClickListener {
                    onItemClickCallback?.onItemClicked(data)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: PromoDomain)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PromoDomain>() {
            override fun areItemsTheSame(oldItem: PromoDomain, newItem: PromoDomain): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PromoDomain, newItem: PromoDomain): Boolean {
                return oldItem == newItem
            }
        }
    }
}