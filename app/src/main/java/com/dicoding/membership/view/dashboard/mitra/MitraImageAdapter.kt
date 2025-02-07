package com.dicoding.membership.view.dashboard.mitra

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.core.utils.ImageUtils.reduceFileImage
import com.dicoding.core.utils.ImageUtils.uriToFile
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemMultipleImageUpload2Binding

class MitraImageAdapter(private val context: Context) :
    RecyclerView.Adapter<MitraImageAdapter.ViewHolder>() {

    private var imageUrls: List<String> = emptyList()

    inner class ViewHolder(private val binding: ItemMultipleImageUpload2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            try {
                when {
                    imageUrl.startsWith("content://") || imageUrl.startsWith("file://") -> {
                        val file = uriToFile(Uri.parse(imageUrl), context)
                        val compressedFile = reduceFileImage(file)
                        Glide.with(context)
                            .load(compressedFile)
                            .placeholder(R.drawable.image_empty)
                            .error(R.drawable.image_empty)
                            .into(binding.ivPromo)
                    }
                    imageUrl.startsWith("https://") -> {
                        Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.image_empty)
                            .error(R.drawable.image_empty)
                            .into(binding.ivPromo)
                    }
                    else -> binding.ivPromo.setImageResource(R.drawable.image_empty)
                }
            } catch (e: Exception) {
                Log.e("MitraImageAdapter", "Error loading image: ${e.message}")
                binding.ivPromo.setImageResource(R.drawable.image_empty)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMultipleImageUpload2Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    fun submitList(urls: List<String>) {
        imageUrls = urls
        notifyDataSetChanged()
    }
}