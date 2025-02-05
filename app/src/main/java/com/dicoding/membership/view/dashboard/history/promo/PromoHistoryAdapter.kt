package com.dicoding.membership.view.dashboard.history.promo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PromoHistoryAdapter : PagingDataAdapter<PromoHistoryDomain, PromoHistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    var onItemClickCallback: OnItemClickCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("PromoHistoryAdapter", "Binding item at position $position")
        item?.let { holder.bind(it) }
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: PromoHistoryDomain) {
            binding.apply {
                Log.d("PromoHistoryAdapter", "Binding item: ${history.promoName}")

                tvPromoTitle.text = history.promoName
                tvRedCategory.apply {
                    text = history.promoCategory.lowercase()
                    setTextColor(ContextCompat.getColor(context, R.color.orange_100))
                    background.setTint(ContextCompat.getColor(context, R.color.orange_accent))
                }

                tvColourStatus.apply {
                    text = history.status
                    when (history.status.lowercase()) {
                        "active" -> {
                            setTextColor(ContextCompat.getColor(context, R.color.green))
                            background.setTint(ContextCompat.getColor(context, R.color.green_accent))
                        }
                        "redeemed" -> {
                            setTextColor(ContextCompat.getColor(context, com.dicoding.core.R.color.red))
                            val redAccentTransparent = ColorUtils.setAlphaComponent(
                                ContextCompat.getColor(context, R.color.red),
                                64
                            )
                            background.setTint(redAccentTransparent)
                        }
                    }
                }

                tvPromoDate.text = formatDate(history.activationDate)
                tvPromoAuthor.text = history.userName

                // Tambahkan onClickListener
                root.setOnClickListener {
                    onItemClickCallback?.onItemClicked(history)
                }
            }
        }

        private fun formatDate(isoDate: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                val outputFormat = SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale("id"))
                outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

                val date = inputFormat.parse(isoDate)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                isoDate
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(history: PromoHistoryDomain)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PromoHistoryDomain>() {
            override fun areItemsTheSame(oldItem: PromoHistoryDomain, newItem: PromoHistoryDomain): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PromoHistoryDomain, newItem: PromoHistoryDomain): Boolean {
                return oldItem == newItem
            }
        }
    }
}
