package com.dicoding.membership.view.dashboard.history.promo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PromoHistoryAdapter : RecyclerView.Adapter<PromoHistoryAdapter.HistoryViewHolder>() {
    private val histories = mutableListOf<PromoHistoryDomain>()

    fun submitList(newHistories: List<PromoHistoryDomain>) {
        Log.d("PromoHistoryAdapter", "Submitting ${newHistories.size} items to adapter")
        histories.clear()
        histories.addAll(newHistories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = histories.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        Log.d("PromoHistoryAdapter", "Binding item at position $position")
        holder.bind(histories[position])
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: PromoHistoryDomain) {
            binding.apply {
                Log.d("PromoHistoryAdapter", "Binding item: ${history.promoName}")

                // Set promo title
                tvPromoTitle.text = history.promoName

                // Set category
                tvRedCategory.apply {
                    text = history.promoCategory.lowercase()
                    setTextColor(ContextCompat.getColor(context, R.color.orange_100))
                    background.setTint(ContextCompat.getColor(context, R.color.orange_accent))
                }

                // Set member status visibility to GONE since we don't use it
                tvColourStatus.visibility = View.GONE

                // Format and set date
                tvPromoDate.text = formatDate(history.activationDate)

                // Set redeemBy
                tvPromoAuthor.text = history.userName
            }
        }

        private fun formatDate(isoDate: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm, dd MMMM yy", Locale("id"))
                val date = inputFormat.parse(isoDate)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                isoDate
            }
        }
    }
}