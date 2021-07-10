package com.joydeep.flickrimagesearch.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.joydeep.flickrimagesearch.databinding.HolderImageBinding
import com.joydeep.flickrimagesearch.model.Image
import com.joydeep.flickrimagesearch.view.viewholder.ImageHolder

class ImagePagingAdapter : PagingDataAdapter<Image, ImageHolder>(ImageHolderComparator()) {

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it.url) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = HolderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(binding)
    }

    class ImageHolderComparator : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }
}