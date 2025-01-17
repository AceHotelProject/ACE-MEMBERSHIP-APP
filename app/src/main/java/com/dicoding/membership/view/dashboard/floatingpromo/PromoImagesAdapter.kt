package com.dicoding.membership.view.dashboard.floatingpromo

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemMultipleImageUploadBinding

@Suppress("DEPRECATION")
class PromoImagesAdapter : RecyclerView.Adapter<PromoImagesAdapter.ImageViewHolder>() {
    private val images = mutableListOf<Uri>()

    fun addImage(uri: Uri) {
        images.add(uri)
        notifyItemInserted(images.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemMultipleImageUploadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    inner class ImageViewHolder(private val binding: ItemMultipleImageUploadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            when {
                // Handle content:// atau file:// URI
                uri.scheme == "content" || uri.scheme == "file" -> {
                    Glide.with(itemView.context)
                        .load(uri)
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(binding.ivPromo)
                }
                // Handle remote URL (http:// atau https://)
                uri.toString().startsWith("http") -> {
                    Glide.with(itemView.context)
                        .load(uri.toString())
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(binding.ivPromo)
                }
                // Fallback untuk kasus lainnya
                else -> {
                    Glide.with(itemView.context)
                        .load(uri)
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(binding.ivPromo)
                }
            }

            binding.tvRemove.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    images.removeAt(pos)
                    notifyItemRemoved(pos)
                    if (images.isEmpty()) {
                        (itemView.context as? StaffAddPromoActivity)?.onImagesEmpty()
                    } else {
                        (itemView.context as? StaffAddPromoActivity)?.updateDotsAfterDeletion()
                    }
                }
            }
        }
    }
}