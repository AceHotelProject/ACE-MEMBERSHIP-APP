package com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearchfilter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemFilterChipBinding

class FilterChipAdapter(
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<FilterChipAdapter.ViewHolder>() {

    private var items = listOf<String>()
    private var selectedPosition = 0 // Default 'All' selected

    fun setSelectedPosition(position: Int) {
        if (position in items.indices) {  // Validasi posisi
            val previousSelected = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            // Trigger callback dengan item yang sesuai
            onItemSelected(if (items[position] == "Semua") "" else items[position])
        }
    }

    fun getSelectedPosition(): Int = selectedPosition

    inner class ViewHolder(private val binding: ItemFilterChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(item: String, position: Int) {
            binding.itemFilterChip.apply {
                text = item
                isSelected = position == selectedPosition

                // Atur background berdasarkan status selected
                setBackgroundResource(
                    if (isSelected) R.drawable.custom_background_chip_category_selected
                    else R.drawable.custom_button_light_grey
                )

                // Ambil warna dari resource dengan ContextCompat
                val textColor = if (isSelected)
                    ContextCompat.getColor(context, R.color.orange_100)
                else
                    Color.BLACK
                setTextColor(textColor)

                setOnClickListener {
                    val previousSelected = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selectedPosition)
                    onItemSelected(if (item == "Semua") "" else item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFilterChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }
}