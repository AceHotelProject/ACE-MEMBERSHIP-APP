package com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemHistoryPoinBinding
import com.dicoding.membership.view.dashboard.history.poin.detail.HistoryDetailPoinActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class SearchPointHistoryAdapter(private val userId: String) :
    RecyclerView.Adapter<SearchPointHistoryAdapter.ViewHolder>() {

    private val items = mutableListOf<PointHistory>()

    class ViewHolder(private val binding: ItemHistoryPoinBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PointHistory, userId: String) {
            val isReceiving = item.to.id == userId
            with(binding) {
                textTitle.text = if (isReceiving) "Terima Poin" else "Transfer Poin"
                itemJumlahPoin.text = item.amount.toString()
                itemJumlahPoin.setTextColor(
                    if (isReceiving)
                        ContextCompat.getColor(itemView.context, R.color.green)
                    else
                        ContextCompat.getColor(itemView.context, R.color.black)
                )

                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale("id"))
                outputFormat.timeZone = TimeZone.getDefault()

                val date = inputFormat.parse(item.createdAt)
                itemWaktu.text = date?.let { outputFormat.format(it) }

                itemNama.text = if (isReceiving) item.from.name else item.to.name

                coinIcon.setImageResource(
                    if (isReceiving) R.drawable.icon_copper_coin_green
                    else R.drawable.icon_copper_coin_black
                )

                root.setOnClickListener {
                    val intent = Intent(itemView.context, HistoryDetailPoinActivity::class.java).apply {
                        putExtra("point_history", item)
                        putExtra("is_receiving", isReceiving)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemHistoryPoinBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], userId)
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<PointHistory>) {
        items.clear()
        items.addAll(newItems.reversed())
        notifyDataSetChanged()
    }
}