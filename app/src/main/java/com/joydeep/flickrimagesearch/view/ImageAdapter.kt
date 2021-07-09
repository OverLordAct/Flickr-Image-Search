package com.joydeep.flickrimagesearch.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.joydeep.flickrimagesearch.databinding.HolderImageBinding
import com.joydeep.flickrimagesearch.model.Photo

class ImageAdapter : PagingDataAdapter<Photo, ImageHolder>(ImageHolderComparator()) {

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(getItem(position)?.url ?: throw IllegalArgumentException("Item not found"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = HolderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(binding)
    }

    class ImageHolderComparator : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}