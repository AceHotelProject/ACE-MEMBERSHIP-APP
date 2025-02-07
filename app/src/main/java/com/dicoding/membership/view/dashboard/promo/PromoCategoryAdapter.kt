package com.dicoding.membership.view.dashboard.promo

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.membership.R
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

                setBackgroundResource(R.drawable.custom_button_white)

                setBackgroundTintList(
                    ColorStateList.valueOf(
                        if (isSelected)
                            ContextCompat.getColor(context, R.color.orange_100)
                        else
                            Color.WHITE
                    ))

                setTextColor(
                    if (isSelected) Color.WHITE
                    else Color.BLACK // sesuaikan dengan warna default yang diinginkan
                )

                setOnClickListener {
                    val previousSelected = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selectedPosition)

                    val selectedCategory = if (category == "All") "" else category
                    Log.d("PromoCategoryAdapter", "Selected category: $selectedCategory")
                    onCategorySelected(selectedCategory)
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