package com.dicoding.membership.view.dashboard.home.member.mlevel

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemMembershipCategoryBinding

class MembershipLevelAdapter : RecyclerView.Adapter<MembershipLevelAdapter.ViewHolder>() {
    private val memberships = mutableListOf<MembershipResponse>()
    private var selectedPosition = -1
    private var onItemSelectedCallback: ((MembershipResponse) -> Unit)? = null

    fun setOnItemSelectedCallback(callback: (MembershipResponse) -> Unit) {
        onItemSelectedCallback = callback
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
        holder.bind(memberships[position], position)
    }

    inner class ViewHolder(private val binding: ItemMembershipCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(membership: MembershipResponse, position: Int) {
            val context = binding.root.context
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

                // Handle TnC points
                createTncRows(context, membership.tnc ?: emptyList())

                // Hide the three dots menu as per requirement
                btnDot.visibility = android.view.View.GONE

                // Set click listener for the whole item
                root.setOnClickListener {
                    val previousSelected = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selectedPosition)
                    onItemSelectedCallback?.invoke(membership)
                }

                // Update selection state
                updateSelectionState(context, position == selectedPosition)
            }
        }

        private fun updateSelectionState(context: Context, isSelected: Boolean) {
            val normalTextColor = ContextCompat.getColor(context, R.color.black)
            val selectedTextColor = ContextCompat.getColor(context, R.color.orange_100)

            binding.apply {
                // Update background
                layoutItem.setBackgroundResource(
                    if (isSelected) R.drawable.custom_background_level_member_selected
                    else R.drawable.custom_background_level_member_non_selected
                )

                // Update price container background
                priceContainer.setBackgroundResource(
                    if (isSelected) R.drawable.custom_background_level_member_price_selected
                    else R.drawable.custom_background_light_grey
                )

                // Update text colors
                tvPackagePriceLabel1.setTextColor(if (isSelected) selectedTextColor else normalTextColor)
                tvPackagePrice1.setTextColor(if (isSelected) selectedTextColor else normalTextColor)
            }
        }

        private fun createTncRows(context: Context, tncList: List<String>) {
            binding.tableTnc.removeAllViews()
            tncList.forEach { tnc ->
                val row = TableRow(context).apply {
                    layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                val dot = ImageView(context).apply {
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                        marginEnd = 8
                    }
                    setBackgroundResource(R.drawable.icons_dot)
                }

                val text = TextView(context).apply {
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    setTextAppearance(context, R.style.textStyleMedium_14)
                    text = tnc
                }

                row.addView(dot)
                row.addView(text)
                binding.tableTnc.addView(row)
            }
        }
    }
}