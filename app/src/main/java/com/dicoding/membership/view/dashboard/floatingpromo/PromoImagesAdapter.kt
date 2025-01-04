package com.dicoding.membership.view.dashboard.floatingpromo

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.membership.databinding.ItemMultipleImageUploadBinding

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
            binding.ivPromo.setImageURI(uri)

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