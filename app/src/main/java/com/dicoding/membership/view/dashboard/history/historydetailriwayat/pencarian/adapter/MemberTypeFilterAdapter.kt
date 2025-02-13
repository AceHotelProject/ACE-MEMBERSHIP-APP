package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemFilterChipBinding

class MemberTypeFilterAdapter(
    private var selectedFilter: String? = null,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<MemberTypeFilterAdapter.ViewHolder>() {

    private var items: List<String> = emptyList()

    fun setData(memberTypes: List<String>) {
        items = memberTypes
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemFilterChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(memberType: String, isSelected: Boolean) {
            binding.itemFilterChip.apply {
                text = memberType
                background = ContextCompat.getDrawable(
                    context,
                    if (isSelected) R.drawable.custom_orange_accent_border_orange
                    else R.drawable.custom_button_light_grey
                )
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (isSelected) R.color.orange_100 else R.color.black
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFilterChipBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, item == selectedFilter)

        holder.itemView.setOnClickListener {
            selectedFilter = if (selectedFilter == item) null else item
            notifyDataSetChanged()
            onItemSelected(item)
        }
    }

    override fun getItemCount(): Int = items.size
}