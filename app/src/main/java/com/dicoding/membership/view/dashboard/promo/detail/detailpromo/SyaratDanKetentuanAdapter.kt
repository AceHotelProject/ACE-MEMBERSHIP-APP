package com.dicoding.membership.view.dashboard.promo.detail.detailpromo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.membership.databinding.ItemDetailSyaratDanKetentuanBinding

class SyaratDanKetentuanAdapter(private val tncList: List<String>) :
    RecyclerView.Adapter<SyaratDanKetentuanAdapter.TncViewHolder>() {

    init {
        Log.d("TnCAdapter", "Adapter initialized with ${tncList.size} items")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TncViewHolder {
        Log.d("TnCAdapter", "Creating view holder")
        val binding = ItemDetailSyaratDanKetentuanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TncViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TncViewHolder, position: Int) {
        Log.d("TnCAdapter", "Binding item at position $position: ${tncList[position]}")
        holder.bind(tncList[position])
    }

    override fun getItemCount(): Int {
        Log.d("TnCAdapter", "Getting item count: ${tncList.size}")
        return tncList.size
    }

    class TncViewHolder(private val binding: ItemDetailSyaratDanKetentuanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tnc: String) {
            Log.d("TnCAdapter", "Binding TnC text: $tnc")
            binding.tvTnc.text = tnc
        }
    }
}
