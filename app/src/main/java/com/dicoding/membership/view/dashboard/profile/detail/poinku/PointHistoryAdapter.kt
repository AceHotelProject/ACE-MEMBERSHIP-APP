package com.dicoding.membership.view.dashboard.profile.detail.poinku

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemHistoryPoinBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PointHistoryAdapter(private val userId: String) : RecyclerView.Adapter<PointHistoryAdapter.ViewHolder>() {
    private val items = mutableListOf<PointHistory>()

    class ViewHolder(private val binding: ItemHistoryPoinBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PointHistory, userId: String) {
            val isReceiving = item.to == userId
            with(binding) {
                textTitle.text = if (isReceiving) "Terima Poin" else "Transfer Poin"
                itemJumlahPoin.text = item.amount.toString()
                itemJumlahPoin.setTextColor(
                    if (isReceiving)
                        ContextCompat.getColor(itemView.context, R.color.green)
                    else
                        ContextCompat.getColor(itemView.context, R.color.black)
                )

                //date format configuration
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale("id"))
                outputFormat.timeZone = TimeZone.getDefault()

                val date = inputFormat.parse(item.createdAt)
                itemWaktu.text = date?.let { outputFormat.format(it) }


                itemNama.text = "Benny Kurniawan" //if (isReceiving) item.from else item.to

                // In ViewHolder bind function:
                binding.coinIcon.setImageResource(
                    if (isReceiving) R.drawable.icon_copper_coin_green
                    else R.drawable.icon_copper_coin_black
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryPoinBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], userId)
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<PointHistory>) {
        items.clear()
        items.addAll(newItems.reversed()) // Reverse the list
        notifyDataSetChanged()
    }
}