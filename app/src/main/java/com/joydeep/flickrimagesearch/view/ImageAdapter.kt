package com.joydeep.flickrimagesearch.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.joydeep.flickrimagesearch.databinding.HolderImageBinding
import com.joydeep.flickrimagesearch.model.PhotoEntity
import com.joydeep.flickrimagesearch.model.PhotoEntity2

class ImageAdapter : PagingDataAdapter<PhotoEntity2, ImageHolder>(ImageHolderComparator()) {

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it.url) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = HolderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(binding)
    }

    class ImageHolderComparator : DiffUtil.ItemCallback<PhotoEntity2>() {
        override fun areItemsTheSame(oldItem: PhotoEntity2, newItem: PhotoEntity2): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: PhotoEntity2, newItem: PhotoEntity2): Boolean {
            return oldItem == newItem
        }
    }
}