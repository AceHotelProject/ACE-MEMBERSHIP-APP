package com.dicoding.membership.view.dashboard.member.listeditmember

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.membership.databinding.ItemMembershipCategoryBinding

class MembershipAdapter : RecyclerView.Adapter<MembershipAdapter.ViewHolder>() {
    private val memberships = mutableListOf<MembershipResponse>()

    // Optional: Add click listener if needed
    private var onItemClickCallback: ((MembershipResponse) -> Unit)? = null

    fun setOnItemClickCallback(callback: (MembershipResponse) -> Unit) {
        onItemClickCallback = callback
    }

    fun submitList(newMemberships: List<MembershipResponse>) {
        memberships.clear()
        memberships.addAll(newMemberships)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMembershipCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = memberships.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(memberships[position])
    }

    inner class ViewHolder(private val binding: ItemMembershipCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(membership: MembershipResponse) {
            binding.apply {
                // Set package title
                tvPackageTitle1.text = membership.type ?: "Unknown Type"

                // Set duration
                tvPromoDate.text = "Berlaku ${membership.duration ?: 0} Bulan"

                // Set price with formatting
                tvPackagePrice1.text = "Rp ${(membership.price ?: 0).toString()
                    .reversed()
                    .chunked(3)
                    .joinToString(".")
                    .reversed()}"

                // Handle TnC points (the bullet points)
                val tncViews = listOf(
                    tvPackageDescription11,
                    tvPackageDescription12,
                    tvPackageDescription13
                )

                // Safely handle null TnC list
                membership.tnc?.forEachIndexed { index, tnc ->
                    if (index < tncViews.size) {
                        tncViews[index].text = tnc
                        tncViews[index].visibility = View.VISIBLE
                    }
                }

                // Hide unused TnC views
                val tncSize = membership.tnc?.size ?: 0
                for (i in tncSize until tncViews.size) {
                    tncViews[i].visibility = View.GONE
                }

                // Optional: Set click listener
                root.setOnClickListener {
                    onItemClickCallback?.invoke(membership)
                }
            }
        }
    }
}