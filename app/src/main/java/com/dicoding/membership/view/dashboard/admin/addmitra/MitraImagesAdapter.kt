package com.dicoding.membership.view.dashboard.admin.addmitra

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ItemMultipleImageUploadBinding

@Suppress("DEPRECATION")
class MitraImagesAdapter : RecyclerView.Adapter<MitraImagesAdapter.ImageViewHolder>() {
    private val images = mutableListOf<Uri>()

    fun getCurrentImages(): List<Uri> = images.toList()

    fun addImage(uri: Uri) {
        images.add(uri)
        notifyItemInserted(images.size - 1)
    }

    fun removeImage(position: Int) {
        if (position in 0 until images.size) {
            images.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, images.size)
        }
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

        private fun String.decodeUrl(): String {
            return this.replace("\\u003d", "=").replace("\\u0026", "&")
        }

        fun bind(uri: Uri) {
            when {
                uri.scheme == "content" || uri.scheme == "file" -> {
                    Glide.with(itemView.context)
                        .load(uri)
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(binding.ivPromo)
                }
                uri.toString().startsWith("http") -> {
                    Glide.with(itemView.context)
                        .load(uri.toString())
                        .placeholder(R.drawable.image_empty)
                        .error(R.drawable.image_empty)
                        .into(binding.ivPromo)
                }
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
                    val imageUri = images[pos]
                    if (imageUri.toString().startsWith("https://")) {
                        (itemView.context as? AddMitraActivity)?.let { activity ->
                            activity.deleteImage(imageUri.toString().decodeUrl(), pos)
                        }
                    } else {
                        removeImage(pos)
                        handleEmptyState()
                    }
                }
            }
        }

        private fun handleEmptyState() {
            (itemView.context as? AddMitraActivity)?.let { activity ->
                if (images.isEmpty()) {
                    activity.onImagesEmpty()
                } else {
                    activity.selectedImages.clear()
                    activity.selectedImages.addAll(images)
                    activity.updateDotsAfterDeletion()
                }
            }
        }
    }
}