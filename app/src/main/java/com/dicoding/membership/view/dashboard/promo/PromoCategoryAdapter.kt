package com.dicoding.membership.view.dashboard.promo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.membership.databinding.ItemButtonPromoBinding

class PromoCategoryAdapter(
    private val onCategorySelected: (String) -> Unit
) : RecyclerView.Adapter<PromoCategoryAdapter.ViewHolder>() {

    private val categories = listOf(
        "All", "Hotel", "Penginapan", "Market", "Restoran",
        "Hiburan", "Sekolah", "Kesehatan", "Pariwisata", "Gym"
    )

    private var selectedPosition = 0 // Default 'All' selected

    inner class ViewHolder(private val binding: ItemButtonPromoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String, position: Int) {
            binding.customButtonPromo.apply {
                text = category
                isSelected = position == selectedPosition

                setOnClickListener {
                    val previousSelected = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selectedPosition)

                    // Pass empty string for "All", otherwise pass category name
                    onCategorySelected(if (category == "All") "" else category)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemButtonPromoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size
}