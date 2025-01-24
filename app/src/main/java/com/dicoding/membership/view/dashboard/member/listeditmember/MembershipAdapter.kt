package com.dicoding.membership.view.dashboard.member.listeditmember

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemMembershipCategoryBinding

class MembershipAdapter : RecyclerView.Adapter<MembershipAdapter.ViewHolder>() {
    private val memberships = mutableListOf<MembershipResponse>()
    private var onEditClickCallback: ((MembershipResponse) -> Unit)? = null
    private var onDeleteClickCallback: ((MembershipResponse) -> Unit)? = null

    fun setOnEditClickCallback(callback: (MembershipResponse) -> Unit) {
        onEditClickCallback = callback
    }

    fun setOnDeleteClickCallback(callback: (MembershipResponse) -> Unit) {
        onDeleteClickCallback = callback
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

                // Handle TnC points using dynamic TableRow creation
                createTncRows(binding.root.context, membership.tnc ?: emptyList())

                // Three dots menu
                btnDot.setOnClickListener { view ->
                    showPopupMenu(view, membership)
                }
            }
        }

        private fun showPopupMenu(view: View, membership: MembershipResponse) {
            PopupMenu(view.context, view).apply {
                inflate(R.menu.membership_options_menu)

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> {
                            onEditClickCallback?.invoke(membership)
                            true
                        }
                        R.id.menu_delete -> {
                            onDeleteClickCallback?.invoke(membership)
                            true
                        }
                        else -> false
                    }
                }
                show()
            }
        }

        private fun createTncRows(context: Context, tncList: List<String>) {
            // Clear existing rows first
            binding.tableTnc.removeAllViews()

            // Create new rows for each TnC
            tncList.forEach { tnc ->
                val tableRow = TableRow(context).apply {
                    layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // Create the dot ImageView
                val dotImage = ImageView(context).apply {
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                        marginEnd = 8.dpToPx(context)
                    }
                    setBackgroundResource(R.drawable.icons_dot)
                }

                // Create the TextView for TnC
                val tncText = TextView(context).apply {
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f
                    )

                    // Handle different API levels
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setTextAppearance(R.style.textStyleMedium_14)
                    } else {
                        setTextAppearance(context, R.style.textStyleMedium_14)
                    }

                    text = tnc
                    maxLines = Int.MAX_VALUE
                    isSingleLine = false
                    isHorizontalScrollBarEnabled = false
                }
                // Add views to TableRow
                tableRow.addView(dotImage)
                tableRow.addView(tncText)

                // Add TableRow to TableLayout
                binding.tableTnc.addView(tableRow)
            }
        }

        private fun Int.dpToPx(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }
    }
}